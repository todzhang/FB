# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: Mcl_Cmd_AppCompat_DataHandler.py


def DataHandlerMain(namespace, InputFilename, OutputFilename):
    import mcl.imports, mcl.data.Input, mcl.data.Output, mcl.status, mcl.target, mcl.object.Message
    mcl.imports.ImportNamesWithNamespace(namespace, 'pc.cmd.appcompat', globals())
    input = mcl.data.Input.GetInput(InputFilename)
    output = mcl.data.Output.StartOutput(OutputFilename, input)
    output.Start('AppCompat', 'appcompat', [])
    msg = mcl.object.Message.DemarshalMessage(input.GetData())
    if input.GetStatus() != mcl.status.MCL_SUCCESS:
        errorMsg = msg.FindMessage(mcl.object.Message.MSG_KEY_RESULT_ERROR)
        moduleError = errorMsg.FindU32(mcl.object.Message.MSG_KEY_RESULT_ERROR_MODULE)
        osError = errorMsg.FindU32(mcl.object.Message.MSG_KEY_RESULT_ERROR_OS)
        output.RecordModuleError(moduleError, osError, errorStrings)
        output.EndWithStatus(input.GetStatus())
        return True
    result = Result()
    result.Demarshal(msg)
    from mcl.object.XmlOutput import XmlOutput
    xml = XmlOutput()
    xml.Start('AppCompat')
    statusStr = 'UNKNOWN'
    if result.status == RESULT_STATUS_SUCCESS:
        statusStr = 'SUCCESS'
    elif result.status == RESULT_STATUS_FAILURE_CLEAN:
        statusStr = 'FAILURE'
    elif result.status == RESULT_STATUS_FAILURE_MANUAL_INTERVENTION_REQUIRED:
        statusStr = 'FAILURE_AND_MANUAL_CLEANUP_NEEDED'
    xml.AddAttribute('status', statusStr + ' (%u)' % result.status)
    output.RecordXml(xml)
    output.EndWithStatus(mcl.target.CALL_SUCCEEDED)
    return True


if __name__ == '__main__':
    import sys
    try:
        namespace, InputFilename, OutputFilename = sys.argv[1:]
    except:
        print '%s <namespace> <input filename> <output filename>' % sys.argv[0]
        sys.exit(1)

    if DataHandlerMain(namespace, InputFilename, OutputFilename) != True:
        sys.exit(-1)
# okay decompiling D:\work\malware\bvp47\FB\Resources\Pc\PyScripts\DataHandlers\Mcl_Cmd_AppCompat_DataHandler.pyo
