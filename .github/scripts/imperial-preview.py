import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from pathlib import Path

PACKAGE = "com.davidsm24.fefwmanager"
OUTPUT = Path(sys.argv[1])
OUTPUT.mkdir(exist_ok=True, parents=True)


def adb(*args, capture=False):
    return subprocess.run(["adb", *args], check=True, capture_output=capture, timeout=30).stdout


def tap(*, text=None, resource=None):
    for _ in range(6):
        adb("shell", "uiautomator", "dump", "/sdcard/imperial-preview.xml", capture=True)
        tree = ET.fromstring(adb("exec-out", "cat", "/sdcard/imperial-preview.xml", capture=True))
        for node in tree.iter("node"):
            if text is not None and node.get("text") != text:
                continue
            if resource is not None and node.get("resource-id") != resource:
                continue
            if node.get("enabled") != "true":
                continue
            left, top, right, bottom = (int(x) for x in re.findall(r"\d+", node.get("bounds", "")))
            if right > left and bottom > top:
                adb("shell", "input", "tap", str((left + right) // 2), str((top + bottom) // 2))
                return
        time.sleep(0.25)
    raise RuntimeError(f"Preview element unavailable: {text or resource}")


def control(name):
    tap(resource=f"{PACKAGE}:id/{name}")


def shot(name):
    time.sleep(0.65)
    (OUTPUT / f"{name}.png").write_bytes(adb("exec-out", "screencap", "-p", capture=True))


adb("shell", "am", "force-stop", PACKAGE)
adb("shell", "am", "start", "-W", "-n", f"{PACKAGE}/.home.MainActivity")
shot("01-sendas")
tap(text="Cai", resource=f"{PACKAGE}:id/route_name")
shot("02-senda")
control("manage_mission")
shot("03-mision")
tap(resource="android:id/button2")
control("manage_units")
control("add_unit")
shot("04-selector")
tap(text="Alexandra", resource=f"{PACKAGE}:id/unit_name")
control("add_unit")
tap(text="Benditz", resource=f"{PACKAGE}:id/unit_name")
shot("05-unidades")
control("back_to_route")
control("manage_supports")
control("add_support")
tap(text="Alexandra", resource=f"{PACKAGE}:id/unit_name")
tap(text="Benditz", resource=f"{PACKAGE}:id/unit_name")
shot("06-apoyos")
adb("shell", "am", "force-stop", PACKAGE)
adb("shell", "wm", "size", "720x1280")
adb("shell", "wm", "density", "360")
adb("shell", "settings", "put", "system", "font_scale", "1.3")
adb("shell", "am", "start", "-W", "-n", f"{PACKAGE}/.home.MainActivity")
tap(text="Cai", resource=f"{PACKAGE}:id/route_name")
control("manage_mission")
shot("07-movil-estrecho")
tap(resource="android:id/button2")
