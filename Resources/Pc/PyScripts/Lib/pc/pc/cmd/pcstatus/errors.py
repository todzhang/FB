# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: errors.py
import mcl.status
ERR_SUCCESS = mcl.status.MCL_SUCCESS
ERR_INVALID_PARAM = mcl.status.framework.ERR_START
ERR_MARSHAL_FAILED = mcl.status.framework.ERR_START + 1
ERR_FIND_FAILED = mcl.status.framework.ERR_START + 2
ERR_OPEN_FAILED = mcl.status.framework.ERR_START + 3
ERR_READ_FAILED = mcl.status.framework.ERR_START + 4
ERR_DATA_NOT_VALID = mcl.status.framework.ERR_START + 5
errorStrings = {ERR_INVALID_PARAM: 'Invalid parameter(s)', 
   ERR_MARSHAL_FAILED: 'Failed to marshal data', 
   ERR_FIND_FAILED: 'Failed to find status from id', 
   ERR_OPEN_FAILED: 'Failed to open status location', 
   ERR_READ_FAILED: 'Failed to read data from status location', 
   ERR_DATA_NOT_VALID: "Status data doesn't match expected format"}
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Lib\pc\pc\cmd\pcstatus\errors.pyo
