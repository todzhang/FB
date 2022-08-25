# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: type_Result.py
from types import *
RESULT_STATUS_SUCCESS = 0
RESULT_STATUS_FAILURE_CLEAN = 1
RESULT_STATUS_FAILURE_MANUAL_INTERVENTION_REQUIRED = 2

class Result:

    def __init__(self):
        self.__dict__['status'] = 0

    def __getattr__(self, name):
        if name == 'status':
            return self.__dict__['status']
        raise AttributeError("Attribute '%s' not found" % name)

    def __setattr__(self, name, value):
        if name == 'status':
            self.__dict__['status'] = value
        else:
            raise AttributeError("Attribute '%s' not found" % name)

    def Marshal(self, mmsg):
        from mcl.object.Message import MarshalMessage
        submsg = MarshalMessage()
        submsg.AddU8(MSG_KEY_RESULT_STATUS, self.__dict__['status'])
        mmsg.AddMessage(MSG_KEY_RESULT, submsg)

    def Demarshal(self, dmsg, instance=-1):
        import mcl.object.Message
        msgData = dmsg.FindData(MSG_KEY_RESULT, mcl.object.Message.MSG_TYPE_MSG, instance)
        submsg = mcl.object.Message.DemarshalMessage(msgData)
        self.__dict__['status'] = submsg.FindU8(MSG_KEY_RESULT_STATUS)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Lib\pc\pc\cmd\appcompat\type_Result.pyo
