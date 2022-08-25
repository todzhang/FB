package ddb.gui;

public enum Mnemonics {
   APPEND('A'),
   APPLY('A'),
   CANCEL('C'),
   CLEAR('e'),
   COPY('C'),
   CUSTOMIZE('C'),
   CUT('t'),
   DELETE('D'),
   DISMISS('D'),
   EDIT('E'),
   EXECUTE('E'),
   EXIT('x'),
   EXPORT('E'),
   FILE('F'),
   FIND('F'),
   FIRST('F'),
   GOTO('G'),
   HELP('H'),
   IMPORT('I'),
   INSERT('I'),
   LAST('L'),
   NEW('N'),
   NEXT('N'),
   NO('N'),
   OK('O'),
   OPEN('O'),
   OPTIONS('O'),
   PASTE('P'),
   PREVIOUS('P'),
   PRINT('P'),
   PRINT_PREVIEW('v'),
   QUERY('Q'),
   REDO('R'),
   RENAME('R'),
   RESET('R'),
   RETRY('y'),
   REVERT('v'),
   SAVE('S'),
   SAVEAS('A'),
   SELECT_ALL('A'),
   SELECTED('S'),
   SORT('S'),
   SUBMIT('S'),
   UNDO('U'),
   UPDATE('U'),
   VIEW('V'),
   YES('Y');

   char character;

   private Mnemonics(char c) {
      this.character = c;
   }

   public char getCharacter() {
      return this.character;
   }
}
