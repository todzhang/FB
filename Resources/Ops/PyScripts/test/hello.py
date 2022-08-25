
import dsz, dsz.control, dsz.cmd, os.path, dsz.version
import sys
import ops.data, ops.cmd
from ops.pprint import pprint
import datetime
import time
import ops.timehelper

def eventfilter(id, info='', num=10000, eventlog='system', color=dsz.DEFAULT, max=100, source=None):
    eventcmd = ops.cmd.getDszCommand('eventlogfilter', log=eventlog, id=id, num=num, max=max)
    eventobject = eventcmd.execute()
    for record in eventobject.record:
        if ((source is not None) and (record.source != source)):
            continue
        recdict = {'date': record.datewritten, 'time': record.timewritten, 'id': record.id, 'num': record.number, 'eventlog': eventlog, 'info': info, 'process': None, 'host': None, 'title': None, 'code': None, 'type': None, 'description': None, 'user': None}
        if (id == 1074):
            if dsz.version.checks.windows.IsVistaOrGreater():
                recdict['user'] = record.string[6].value
            recdict['process'] = record.string[0].value
            recdict['host'] = record.string[1].value
            recdict['title'] = record.string[2].value
            recdict['code'] = record.string[3].value
            recdict['type'] = record.string[4].value
            recdict['description'] = record.string[5].value
        recdict['color'] = color
        record_list.append(recdict)

def doeventlogs():
    global record_list
    record_list = []
    color_list = []
    # import pdb;
    # pdb.set_trace()
    # eventfilter(6005, info='Start of event log service', color=dsz.DEFAULT)
    # eventfilter(6006, info='Event service stopped (clean shutdown)', color=dsz.DEFAULT)
    # eventfilter(6008, info='System shut down unexpectedly (dirty shutdown)', color=dsz.ERROR)
    # eventfilter(6009, info='System boot', color=dsz.GOOD)
    # eventfilter(1001, info='BugCheck', color=dsz.ERROR)
    # eventfilter(1074, info='Shutdown info', color=dsz.WARNING)
    # eventfilter(109, info='Kernel-Power: Shutdown transition', color=dsz.DEFAULT)
    # eventfilter(42, info='Kernel-Power: Informational', source='Microsoft-Windows-Kernel-Power')
    # eventfilter(41, info='Kernel-Power: Critical', color=dsz.ERROR, source='Microsoft-Windows-Kernel-Power')
    # eventfilter(13, info='Kernel: Stop', color=dsz.DEFAULT, source='Microsoft-Windows-Kernel-General')
    eventfilter(12, info='Kernel: Start', color=dsz.DEFAULT, source='Microsoft-Windows-Kernel-General')
    record_list.sort(key=(lambda x: x['eventlog']))
    record_list.sort(key=(lambda x: x['num']))
    for record in record_list:
        color_list.append(record['color'])
    pprint(record_list, header=['Date', 'Time', 'ID', 'Eventlog', 'RecNum', 'Info', 'Process', 'Hostname', 'Title', 'Code', 'Type', 'Description', 'User'], dictorder=['date', 'time', 'id', 'eventlog', 'num', 'info', 'process', 'host', 'title', 'code', 'type', 'description', 'user'], echocodes=color_list)
    print '\n'
    makebootlog(record_list)
    print '\n'

def makebootlog(record_list):
    boot_hist = []
    this_event = []
    for record in record_list:
        if (record['id'] == 6009):
            boot_hist.append(this_event)
            this_event = []
        this_event.append(record)
    boot_hist.append(this_event)
    boot_summary = []
    color_list = []
    for this_event in boot_hist:
        if (len(this_event) == 0):
            continue
        boot = None
        shutdown = None
        reason = []
        crash = False
        uptime = None
        for record in this_event:
            if (record['id'] == 6009):
                boot = ('%s %s' % (record['date'], record['time']))
            elif (record['id'] == 6006):
                shutdown = ('%s %s' % (record['date'], record['time']))
            elif (record['id'] == 6008):
                crash = True
            elif (record['id'] == 1001):
                crash = True
            elif (record['id'] == 1074):
                reason.append(record['title'])
        reason = ','.join(reason)
        boot_summary.append({'boot': boot, 'shutdown': shutdown, 'reason': reason, 'crash': crash, 'uptime': uptime})
        if crash:
            color_list.append(dsz.ERROR)
        else:
            color_list.append(dsz.DEFAULT)
    for boot in boot_summary:
        if ((boot['boot'] is not None) and (boot['shutdown'] is not None)):
            boottime = datetime.datetime(*time.strptime(boot['boot'], '%Y-%m-%d %H:%M:%S')[0:6])
            shutdowntime = datetime.datetime(*time.strptime(boot['shutdown'], '%Y-%m-%d %H:%M:%S')[0:6])
            uptime = (shutdowntime - boottime)
            boot['uptime'] = ops.timehelper.get_age_from_seconds((((uptime.days * 3600) * 24) + uptime.seconds))
    pprint(boot_summary, header=['Boot', 'Shutdown', 'Uptime', 'Reason', 'Crash'], dictorder=['boot', 'shutdown', 'uptime', 'reason', 'crash'], echocodes=color_list)

def main(arguments):
    dsz.ui.Echo('Reboot Eventlogs', dsz.GOOD)
    doeventlogs()

if (__name__ == '__main__'):
    try:
        main(sys.argv[1:])
    except RuntimeError as e:
        dsz.ui.Echo(('\nCaught RuntimeError: %s' % e), dsz.ERROR)