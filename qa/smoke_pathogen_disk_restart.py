#!/usr/bin/env python3
"""Start one ephemeral NeoForge world twice, verify genuine disk persistence.

Runs ONLY in a fresh GitHub Actions runner. RCON is loopback-only, enabled
specifically for this test, with random ephemeral port and runtime-only secret.
No existing users' worlds are opened or modified.
"""
from __future__ import annotations

import os
from pathlib import Path
import re
import secrets
import signal
import socket
import struct
import subprocess
import sys
import time

ROOT = Path(__file__).resolve().parents[1]
GAME_WORLD = "gugugaga-ci-scalar-field-only"
NBT_NAME = "gugugaga_spatial_pathogen_field_v1.dat"
EXTENSION_NBT_NAME = "gugugaga_ecology_extension_v1.dat"
TIMEOUT = int(os.environ.get("GUGU_DISK_STAGE_TIMEOUT", "480"))
READY = re.compile(r"Done \([0-9.,]+s\)! For help, type", re.I)
INIT = "GUGUGAGA_RUNTIME_INIT_OK"
MEMORY = "GUGUGAGA_SPATIAL_SAVEDDATA_ROUNDTRIP_PASS"
MARKERS = {
    "write": "GUGUGAGA_FIELD_DISK_WRITE_PASS",
    "verify": "GUGUGAGA_FIELD_DISK_RESTART_PASS",
}


def get_free_port() -> int:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.bind(("127.0.0.1", 0))
        return int(sock.getsockname()[1])


def prepare_server(port: int, rcon_port: int, password: str):
    # NeoForge ModDevGradle uses runs/server; older layouts can use run.
    properties = (
        "server-ip=127.0.0.1\n"
        f"server-port={port}\n"
        f"level-name={GAME_WORLD}\n"
        "online-mode=false\n"
        "motd=GuguGaga Ephemeral CI Field Test\n"
        "max-players=1\n"
        "view-distance=4\n"
        "simulation-distance=4\n"
        "enable-rcon=true\n"
        f"rcon.port={rcon_port}\n"
        f"rcon.password={password}\n"
        "broadcast-rcon-to-ops=false\n"
        "sync-chunk-writes=true\n"
    )
    for folder in (ROOT / "runs/server", ROOT / "run"):
        folder.mkdir(parents=True, exist_ok=True)
        (folder / "eula.txt").write_text("eula=true\n", encoding="utf-8")
        (folder / "server.properties").write_text(properties, encoding="utf-8")


def exact(sock: socket.socket, n: int) -> bytes:
    chunks = []
    while n > 0:
        chunk = sock.recv(n)
        if not chunk:
            raise ConnectionError("RCON closed unexpectedly")
        chunks.append(chunk)
        n -= len(chunk)
    return b"".join(chunks)


def send(sock: socket.socket, packet_id: int, kind: int, message: str):
    body = struct.pack("<ii", packet_id, kind) + message.encode() + b"\0\0"
    sock.sendall(struct.pack("<i", len(body)) + body)


def receive(sock: socket.socket):
    length = struct.unpack("<i", exact(sock, 4))[0]
    if length < 10 or length > 65536:
        raise ValueError("Invalid local RCON packet size")
    body = exact(sock, length)
    packet_id, kind = struct.unpack("<ii", body[:8])
    return packet_id, kind, body[8:-2].decode(errors="replace")


def rcon(rcon_port: int, secret: str, instruction: str):
    with socket.create_connection(("127.0.0.1", rcon_port), timeout=5) as sock:
        sock.settimeout(7)
        send(sock, 1001, 3, secret)
        authenticated = False
        for _ in range(4):
            response_id, kind, _ = receive(sock)
            if response_id == -1:
                raise RuntimeError("Local RCON auth rejected")
            if response_id == 1001 and kind == 2:
                authenticated = True
                break
        if not authenticated:
            raise RuntimeError("No local RCON auth acknowledgement")
        send(sock, 1002, 2, instruction)
        if instruction != "stop":
            response_id, _, reply = receive(sock)
            if response_id != 1002:
                raise RuntimeError("Unexpected RCON reply id")
            return reply
    return ""


def run_phase(phase: str, rcon_port: int, secret: str):
    logfile = ROOT / "build" / f"field-disk-{phase}.log"
    logfile.parent.mkdir(parents=True, exist_ok=True)
    env = os.environ.copy()
    env["GUGU_FIELD_SELFTEST"] = "1"
    env["GUGU_FIELD_DISK_PHASE"] = phase

    with logfile.open("wb") as output:
        proc = subprocess.Popen(
            ["gradle", "--no-daemon", "--console=plain", "runServer"],
            cwd=ROOT, stdout=output, stderr=subprocess.STDOUT,
            start_new_session=True, env=env,
        )
        try:
            deadline = time.monotonic() + TIMEOUT
            while time.monotonic() < deadline:
                time.sleep(2)
                content = logfile.read_text(encoding="utf-8", errors="replace")
                if all(text in content for text in (INIT, MEMORY, MARKERS[phase])) \
                        and READY.search(content):
                    break
                if proc.poll() is not None:
                    raise RuntimeError(f"Server terminated during {phase} boot")
            else:
                raise TimeoutError(f"Server never completed {phase} boot")

            # RCON is loopback-only and lives solely in this ephemeral CI run.
            for attempt in range(25):
                try:
                    rcon(rcon_port, secret, "list")
                    break
                except (OSError, RuntimeError):
                    time.sleep(0.4)
            else:
                raise RuntimeError("Local ephemeral RCON is not available")

            rcon(rcon_port, secret, "save-all flush")
            try:
                rcon(rcon_port, secret, "stop")
            except (ConnectionError, ConnectionResetError, OSError):
                # Minecraft can close the socket while obeying 'stop'.
                pass
            try:
                exit_code = proc.wait(timeout=70)
            except subprocess.TimeoutExpired as exc:
                raise RuntimeError("The local server failed to stop cleanly") from exc
            if exit_code:
                raise RuntimeError(f"Gradle runServer phase {phase} exited {exit_code}")
            print(f"GUGUGAGA_DISK_PHASE_{phase.upper()}_PASS", flush=True)
        except Exception:
            print(f"=== {phase} server log tail ===", flush=True)
            print(logfile.read_text(encoding="utf-8", errors="replace")[-12000:],
                  flush=True)
            raise
        finally:
            if proc.poll() is None:
                try:
                    os.killpg(proc.pid, signal.SIGTERM)
                except ProcessLookupError:
                    pass
                try:
                    proc.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    try:
                        os.killpg(proc.pid, signal.SIGKILL)
                    except ProcessLookupError:
                        pass
                    proc.wait(timeout=5)


def main():
    if os.environ.get("GITHUB_ACTIONS") != "true":
        raise RuntimeError("Disk-restart smoke requires an isolated GitHub Actions runner.")
    password = secrets.token_hex(24)  # Never printed or committed.
    game_port, rcon_port = get_free_port(), get_free_port()
    if game_port == rcon_port:
        raise RuntimeError("Local test ports unexpectedly identical")
    prepare_server(game_port, rcon_port, password)
    run_phase("write", rcon_port, password)
    expected = [
        ROOT / "runs/server" / GAME_WORLD / "data" / NBT_NAME,
        ROOT / "run" / GAME_WORLD / "data" / NBT_NAME,
    ]
    if not any(p.is_file() and p.stat().st_size > 32 for p in expected):
        raise AssertionError("Minecraft did not write the game's actual SavedData NBT file")
    print("GUGUGAGA_REAL_WORLD_NBT_FILE_FOUND", flush=True)
    extension_files = [
        ROOT / "runs/server" / GAME_WORLD / "data" / EXTENSION_NBT_NAME,
        ROOT / "run" / GAME_WORLD / "data" / EXTENSION_NBT_NAME,
    ]
    if not any(p.is_file() and p.stat().st_size > 32 for p in extension_files):
        raise AssertionError("Minecraft did not write the extension SavedData NBT file")
    print("GUGUGAGA_FIVE_FOCUS_EXTENSION_DISK_FOUND", flush=True)
    run_phase("verify", rcon_port, password)
    print("GUGUGAGA_REAL_WORLD_DISK_RESTART_PASS", flush=True)
    return 0


if __name__ == "__main__":
    sys.exit(main())
