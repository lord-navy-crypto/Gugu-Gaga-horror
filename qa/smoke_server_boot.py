#!/usr/bin/env python3
"""Actual v0.10 NeoForge dedicated-server constructor + ready smoke.

Compilation alone is insufficient. No user worlds are accessed: this only
starts a fresh GitHub Actions ephemeral local server, then cleans it up.
"""
from pathlib import Path
import os
import re
import signal
import subprocess
import sys
import time

ROOT = Path(__file__).resolve().parents[1]
LOG = ROOT / "build" / "server-runtime-smoke.log"
MARKER = "GUGUGAGA_RUNTIME_INIT_OK"
SAVEDATA_MARKER = "GUGUGAGA_SPATIAL_SAVEDDATA_ROUNDTRIP_PASS"
FOCUS_MARKER = "GUGUGAGA_FIVE_FOCUS_NATIVE_NBT_PASS"
HEALTH_MARKER = "GUGUGAGA_ONE_HEALTH_NBT_PASS"
READY = re.compile(r"Done \([0-9.,]+s\)! For help, type", re.I)
MAX_SECONDS = int(os.environ.get("GUGU_BOOT_TIMEOUT", "420"))


def main():
    LOG.parent.mkdir(parents=True, exist_ok=True)
    # Cover the modern ModDevGradle default, plus older run/ layout.
    for path in (ROOT / "runs/server", ROOT / "run"):
        path.mkdir(parents=True, exist_ok=True)
        (path / "eula.txt").write_text("eula=true\n", encoding="utf-8")
    print("Launching actual v0.10 NeoForge dedicated server.", flush=True)
    with LOG.open("wb") as logfile:
        proc = subprocess.Popen(
            ["gradle", "--no-daemon", "--console=plain", "runServer"],
            cwd=ROOT, stdout=logfile, stderr=subprocess.STDOUT,
            start_new_session=True,
        )
        try:
            deadline = time.monotonic() + MAX_SECONDS
            while time.monotonic() < deadline:
                time.sleep(2)
                log = LOG.read_text(encoding="utf-8", errors="replace")
                integrated_pass = (
                    os.environ.get("GUGU_FIELD_SELFTEST") != "1"
                    or (SAVEDATA_MARKER in log and FOCUS_MARKER in log
                        and HEALTH_MARKER in log)
                )
                if MARKER in log and READY.search(log) and integrated_pass:
                    print("GUGUGAGA_V010_REAL_SERVER_BOOT_PASS", flush=True)
                    return 0
                if proc.poll() is not None:
                    print("FAIL: Gradle or server exited before both ready markers.", flush=True)
                    break
            else:
                print("FAIL: server did not start within bounded timeout.", flush=True)
            print("=== SERVER LOG TAIL ===", flush=True)
            print(LOG.read_text(encoding="utf-8", errors="replace")[-20000:], flush=True)
            return 1
        finally:
            if proc.poll() is None:
                try:
                    os.killpg(proc.pid, signal.SIGTERM)
                except ProcessLookupError:
                    pass
                try:
                    proc.wait(timeout=12)
                except subprocess.TimeoutExpired:
                    try:
                        os.killpg(proc.pid, signal.SIGKILL)
                    except ProcessLookupError:
                        pass
                    proc.wait(timeout=5)


if __name__ == "__main__":
    sys.exit(main())
