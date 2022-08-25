# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: tasking.py
import mcl_platform.tasking
from tasking_dsz import *
_fw = mcl_platform.tasking.GetFramework()
if _fw == 'dsz':
    RPC_INFO_QUERY = dsz.RPC_INFO_QUERY
else:
    raise RuntimeError('Unsupported framework (%s)' % _fw)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Lib\pc\pc\cmd\pcstatus\tasking.pyo
