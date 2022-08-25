
import dsz, dsz.control, dsz.cmd, dsz.lp, dsz.path, dsz.env, dsz.version
import sys
# import ops.data, ops.cmd
# from ops.pprint import pprint
# import util.mac


def main(args):
    if (len(args) > 0):
        dsz.ui.Echo(('\nYou are so cute: %s' % args[0]), dsz.ERROR)
    else:
        dsz.ui.Echo(('\nYou are so cute: %s' % "Hello"), dsz.ERROR)
    return True
if (__name__ == '__main__'):
    try:
        main(sys.argv[1:])
    except RuntimeError as e:
        dsz.ui.Echo(('\nCaught RuntimeError: %s' % e), dsz.ERROR)