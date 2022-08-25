# uncompyle6 version 3.8.0
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.18 (v2.7.18:8d21aa21f2, Apr 20 2020, 13:19:08) [MSC v.1500 32 bit (Intel)]
# Warning: this version of Python has problems handling the Python 3 byte type in constants properly.

# Embedded file name: __init__.py
# Compiled at: 2012-04-28 03:25:42


def IsValidIpAddress(addr):
    import re
    if re.match('^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$', addr) != None or re.match('^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$', addr) != None or re.match('^::$', addr) != None or re.match('^::([a-fA-F0-9]){1,4}(:([a-f]|[A-F]|[0-9]){1,4}){0,6}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}::([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,5}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,1}::([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,4}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,2}::([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,3}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,3}::([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,2}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,4}::([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,1}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,4}::([a-fA-F0-9]){1,4}:([a-fA-F0-9]){1,4}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,5}::([a-fA-F0-9]){1,4}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,6}::$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){5}:[0-9]{1,3}(\\.[0-9]{1,3}){3}$', addr) != None or re.match('^::([0-9]){1,3}(\\.[0-9]{1,3}){3}$', addr) != None or re.match('^::([a-fA-F0-9]){1,4}(:)([0-9]){1,3}(\\.[0-9]{1,3}){3}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}::([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,3}:[0-9]{1,3}(\\.[0-9]{1,3}){3}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,1}::([a-fA-F0-9]){1,4}(:[a-fA-F0-9]){0,2}:[0-9]{1,3}(\\.[0-9]{1,3}){3}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,2}::([a-fA-F0-9]){1,4}(:[a-fA-F0-9]){0,1}:[0-9]{1,3}(\\.[0-9]{1,3}){3}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,3}::([a-fA-F0-9]){1,4}:[0-9]{1,3}(\\.[0-9]{1,3}){3}$', addr) != None or re.match('^([a-fA-F0-9]){1,4}(:([a-fA-F0-9]){1,4}){0,4}::[0-9]{1,3}(\\.[0-9]{1,3}){3}$', addr) != None:
        return True
    else:
        dsz.ui.Echo('Invalid IP address', dsz.ERROR)
        return False
        return
# okay decompiling __init__.pyo
