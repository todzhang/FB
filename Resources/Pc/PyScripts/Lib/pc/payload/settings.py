# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: settings.py.override
import dsz

def CheckConfigLocal():
    if dsz.ui.Prompt('Do you want to configure with FC?', True):
        return False
    else:
        return True


def Finalize(payloadFile):
    return dsz.cmd.Run('python Payload/_Prep.py -args "-action disable -file %s"' % payloadFile)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Lib\pc\payload\settings.pyo
