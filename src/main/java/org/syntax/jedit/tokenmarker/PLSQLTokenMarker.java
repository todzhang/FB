package org.syntax.jedit.tokenmarker;

import org.syntax.jedit.KeywordMap;

public class PLSQLTokenMarker extends SQLTokenMarker {
   private static KeywordMap plsqlKeywords;

   public PLSQLTokenMarker() {
      super(getKeywordMap(), true);
   }

   public static KeywordMap getKeywordMap() {
      if (plsqlKeywords == null) {
         plsqlKeywords = new KeywordMap(true);
         addKeywords();
         addDataTypes();
         addSystemFunctions();
         addOperators();
         addSystemStoredProcedures();
         addSystemTables();
      }

      return plsqlKeywords;
   }

   private static void addKeywords() {
      plsqlKeywords.add("ABORT", (byte)6);
      plsqlKeywords.add("ACCESS", (byte)6);
      plsqlKeywords.add("ADD", (byte)6);
      plsqlKeywords.add("ALTER", (byte)6);
      plsqlKeywords.add("ARRAY", (byte)6);
      plsqlKeywords.add("ARRAY_LEN", (byte)6);
      plsqlKeywords.add("AS", (byte)6);
      plsqlKeywords.add("ASC", (byte)6);
      plsqlKeywords.add("ASSERT", (byte)6);
      plsqlKeywords.add("ASSIGN", (byte)6);
      plsqlKeywords.add("AT", (byte)6);
      plsqlKeywords.add("AUDIT", (byte)6);
      plsqlKeywords.add("AUTHORIZATION", (byte)6);
      plsqlKeywords.add("AVG", (byte)6);
      plsqlKeywords.add("BASE_TABLE", (byte)6);
      plsqlKeywords.add("BEGIN", (byte)6);
      plsqlKeywords.add("BODY", (byte)6);
      plsqlKeywords.add("CASE", (byte)6);
      plsqlKeywords.add("CHAR", (byte)6);
      plsqlKeywords.add("CHAR_BASE", (byte)6);
      plsqlKeywords.add("CHECK", (byte)6);
      plsqlKeywords.add("CLOSE", (byte)6);
      plsqlKeywords.add("CLUSTER", (byte)6);
      plsqlKeywords.add("CLUSTERS", (byte)6);
      plsqlKeywords.add("COLAUTH", (byte)6);
      plsqlKeywords.add("COLUMN", (byte)6);
      plsqlKeywords.add("COMMENT", (byte)6);
      plsqlKeywords.add("COMMIT", (byte)6);
      plsqlKeywords.add("COMPRESS", (byte)6);
      plsqlKeywords.add("CONSTANT", (byte)6);
      plsqlKeywords.add("CONSTRAINT", (byte)6);
      plsqlKeywords.add("COUNT", (byte)6);
      plsqlKeywords.add("CREATE", (byte)6);
      plsqlKeywords.add("CURRENT", (byte)6);
      plsqlKeywords.add("CURRVAL", (byte)6);
      plsqlKeywords.add("CURSOR", (byte)6);
      plsqlKeywords.add("DATABASE", (byte)6);
      plsqlKeywords.add("DATA_BASE", (byte)6);
      plsqlKeywords.add("DATE", (byte)6);
      plsqlKeywords.add("DBA", (byte)6);
      plsqlKeywords.add("DEBUGOFF", (byte)6);
      plsqlKeywords.add("DEBUGON", (byte)6);
      plsqlKeywords.add("DECLARE", (byte)6);
      plsqlKeywords.add("DEFAULT", (byte)6);
      plsqlKeywords.add("DEFINITION", (byte)6);
      plsqlKeywords.add("DELAY", (byte)6);
      plsqlKeywords.add("DELETE", (byte)6);
      plsqlKeywords.add("DESC", (byte)6);
      plsqlKeywords.add("DIGITS", (byte)6);
      plsqlKeywords.add("DISPOSE", (byte)6);
      plsqlKeywords.add("DISTINCT", (byte)6);
      plsqlKeywords.add("DO", (byte)6);
      plsqlKeywords.add("DROP", (byte)6);
      plsqlKeywords.add("DUMP", (byte)6);
      plsqlKeywords.add("ELSE", (byte)6);
      plsqlKeywords.add("ELSIF", (byte)6);
      plsqlKeywords.add("END", (byte)6);
      plsqlKeywords.add("ENTRY", (byte)6);
      plsqlKeywords.add("EXCEPTION", (byte)6);
      plsqlKeywords.add("EXCEPTION_INIT", (byte)6);
      plsqlKeywords.add("EXCLUSIVE", (byte)6);
      plsqlKeywords.add("EXIT", (byte)6);
      plsqlKeywords.add("FALSE", (byte)6);
      plsqlKeywords.add("FETCH", (byte)6);
      plsqlKeywords.add("FILE", (byte)6);
      plsqlKeywords.add("FOR", (byte)6);
      plsqlKeywords.add("FORM", (byte)6);
      plsqlKeywords.add("FROM", (byte)6);
      plsqlKeywords.add("FUNCTION", (byte)6);
      plsqlKeywords.add("GENERIC", (byte)6);
      plsqlKeywords.add("GOTO", (byte)6);
      plsqlKeywords.add("GRANT", (byte)6);
      plsqlKeywords.add("GREATEST", (byte)6);
      plsqlKeywords.add("GROUP", (byte)6);
      plsqlKeywords.add("HAVING", (byte)6);
      plsqlKeywords.add("IDENTIFIED", (byte)6);
      plsqlKeywords.add("IDENTITYCOL", (byte)6);
      plsqlKeywords.add("IF", (byte)6);
      plsqlKeywords.add("IMMEDIATE", (byte)6);
      plsqlKeywords.add("INCREMENT", (byte)6);
      plsqlKeywords.add("INDEX", (byte)6);
      plsqlKeywords.add("INDEXES", (byte)6);
      plsqlKeywords.add("INDICATOR", (byte)6);
      plsqlKeywords.add("INITIAL", (byte)6);
      plsqlKeywords.add("INSERT", (byte)6);
      plsqlKeywords.add("INTERFACE", (byte)6);
      plsqlKeywords.add("INTO", (byte)6);
      plsqlKeywords.add("IS", (byte)6);
      plsqlKeywords.add("LEAST", (byte)6);
      plsqlKeywords.add("LEVEL", (byte)6);
      plsqlKeywords.add("LIMITED", (byte)6);
      plsqlKeywords.add("LOCK", (byte)6);
      plsqlKeywords.add("LONG", (byte)6);
      plsqlKeywords.add("LOOP", (byte)6);
      plsqlKeywords.add("MAX", (byte)6);
      plsqlKeywords.add("MAXEXTENTS", (byte)6);
      plsqlKeywords.add("MIN", (byte)6);
      plsqlKeywords.add("MINUS", (byte)6);
      plsqlKeywords.add("MLSLABEL", (byte)6);
      plsqlKeywords.add("MOD", (byte)6);
      plsqlKeywords.add("MORE", (byte)6);
      plsqlKeywords.add("NEW", (byte)6);
      plsqlKeywords.add("NEXTVAL", (byte)6);
      plsqlKeywords.add("NOAUDIT", (byte)6);
      plsqlKeywords.add("NOCOMPRESS", (byte)6);
      plsqlKeywords.add("NOWAIT", (byte)6);
      plsqlKeywords.add("NULL", (byte)6);
      plsqlKeywords.add("NUMBER_BASE", (byte)6);
      plsqlKeywords.add("OF", (byte)6);
      plsqlKeywords.add("OFFLINE", (byte)6);
      plsqlKeywords.add("ON", (byte)6);
      plsqlKeywords.add("OFF", (byte)6);
      plsqlKeywords.add("ONLINE", (byte)6);
      plsqlKeywords.add("OPEN", (byte)6);
      plsqlKeywords.add("OPTION", (byte)6);
      plsqlKeywords.add("ORDER", (byte)6);
      plsqlKeywords.add("OTHERS", (byte)6);
      plsqlKeywords.add("OUT", (byte)6);
      plsqlKeywords.add("PACKAGE", (byte)6);
      plsqlKeywords.add("PARTITION", (byte)6);
      plsqlKeywords.add("PCTFREE", (byte)6);
      plsqlKeywords.add("PRAGMA", (byte)6);
      plsqlKeywords.add("PRIVATE", (byte)6);
      plsqlKeywords.add("PRIVILEGES", (byte)6);
      plsqlKeywords.add("PROCEDURE", (byte)6);
      plsqlKeywords.add("PUBLIC", (byte)6);
      plsqlKeywords.add("QUOTED_IDENTIFIER", (byte)6);
      plsqlKeywords.add("RAISE", (byte)6);
      plsqlKeywords.add("RANGE", (byte)6);
      plsqlKeywords.add("RECORD", (byte)6);
      plsqlKeywords.add("REF", (byte)6);
      plsqlKeywords.add("RELEASE", (byte)6);
      plsqlKeywords.add("REMR", (byte)6);
      plsqlKeywords.add("RENAME", (byte)6);
      plsqlKeywords.add("RESOURCE", (byte)6);
      plsqlKeywords.add("RETURN", (byte)6);
      plsqlKeywords.add("REVERSE", (byte)6);
      plsqlKeywords.add("REVOKE", (byte)6);
      plsqlKeywords.add("ROLLBACK", (byte)6);
      plsqlKeywords.add("ROW", (byte)6);
      plsqlKeywords.add("ROWLABEL", (byte)6);
      plsqlKeywords.add("ROWNUM", (byte)6);
      plsqlKeywords.add("ROWS", (byte)6);
      plsqlKeywords.add("ROWTYPE", (byte)6);
      plsqlKeywords.add("RUN", (byte)6);
      plsqlKeywords.add("SAVEPOINT", (byte)6);
      plsqlKeywords.add("SCHEMA", (byte)6);
      plsqlKeywords.add("SELECT", (byte)6);
      plsqlKeywords.add("SEPERATE", (byte)6);
      plsqlKeywords.add("SESSION", (byte)6);
      plsqlKeywords.add("SET", (byte)6);
      plsqlKeywords.add("SHARE", (byte)6);
      plsqlKeywords.add("SPACE", (byte)6);
      plsqlKeywords.add("SQL", (byte)6);
      plsqlKeywords.add("SQLCODE", (byte)6);
      plsqlKeywords.add("SQLERRM", (byte)6);
      plsqlKeywords.add("STATEMENT", (byte)6);
      plsqlKeywords.add("STDDEV", (byte)6);
      plsqlKeywords.add("SUBTYPE", (byte)6);
      plsqlKeywords.add("SUCCESSFULL", (byte)6);
      plsqlKeywords.add("SUM", (byte)6);
      plsqlKeywords.add("SYNONYM", (byte)6);
      plsqlKeywords.add("SYSDATE", (byte)6);
      plsqlKeywords.add("TABAUTH", (byte)6);
      plsqlKeywords.add("TABLE", (byte)6);
      plsqlKeywords.add("TABLES", (byte)6);
      plsqlKeywords.add("TASK", (byte)6);
      plsqlKeywords.add("TERMINATE", (byte)6);
      plsqlKeywords.add("THEN", (byte)6);
      plsqlKeywords.add("TO", (byte)6);
      plsqlKeywords.add("TRIGGER", (byte)6);
      plsqlKeywords.add("TRUE", (byte)6);
      plsqlKeywords.add("TYPE", (byte)6);
      plsqlKeywords.add("UID", (byte)6);
      plsqlKeywords.add("UNION", (byte)6);
      plsqlKeywords.add("UNIQUE", (byte)6);
      plsqlKeywords.add("UPDATE", (byte)6);
      plsqlKeywords.add("UPDATETEXT", (byte)6);
      plsqlKeywords.add("USE", (byte)6);
      plsqlKeywords.add("USER", (byte)6);
      plsqlKeywords.add("USING", (byte)6);
      plsqlKeywords.add("VALIDATE", (byte)6);
      plsqlKeywords.add("VALUES", (byte)6);
      plsqlKeywords.add("VARIANCE", (byte)6);
      plsqlKeywords.add("VIEW", (byte)6);
      plsqlKeywords.add("VIEWS", (byte)6);
      plsqlKeywords.add("WHEN", (byte)6);
      plsqlKeywords.add("WHENEVER", (byte)6);
      plsqlKeywords.add("WHERE", (byte)6);
      plsqlKeywords.add("WHILE", (byte)6);
      plsqlKeywords.add("WITH", (byte)6);
      plsqlKeywords.add("WORK", (byte)6);
      plsqlKeywords.add("WRITE", (byte)6);
      plsqlKeywords.add("XOR", (byte)6);
      plsqlKeywords.add("ABS", (byte)7);
      plsqlKeywords.add("ACOS", (byte)7);
      plsqlKeywords.add("ADD_MONTHS", (byte)7);
      plsqlKeywords.add("ASCII", (byte)7);
      plsqlKeywords.add("ASIN", (byte)7);
      plsqlKeywords.add("ATAN", (byte)7);
      plsqlKeywords.add("ATAN2", (byte)7);
      plsqlKeywords.add("CEIL", (byte)7);
      plsqlKeywords.add("CHARTOROWID", (byte)7);
      plsqlKeywords.add("CHR", (byte)7);
      plsqlKeywords.add("CONCAT", (byte)7);
      plsqlKeywords.add("CONVERT", (byte)7);
      plsqlKeywords.add("COS", (byte)7);
      plsqlKeywords.add("COSH", (byte)7);
      plsqlKeywords.add("DECODE", (byte)7);
      plsqlKeywords.add("DEFINE", (byte)7);
      plsqlKeywords.add("FLOOR", (byte)7);
      plsqlKeywords.add("HEXTORAW", (byte)7);
      plsqlKeywords.add("INITCAP", (byte)7);
      plsqlKeywords.add("INSTR", (byte)7);
      plsqlKeywords.add("INSTRB", (byte)7);
      plsqlKeywords.add("LAST_DAY", (byte)7);
      plsqlKeywords.add("LENGTH", (byte)7);
      plsqlKeywords.add("LENGTHB", (byte)7);
      plsqlKeywords.add("LN", (byte)7);
      plsqlKeywords.add("LOG", (byte)7);
      plsqlKeywords.add("LOWER", (byte)7);
      plsqlKeywords.add("LPAD", (byte)7);
      plsqlKeywords.add("LTRIM", (byte)7);
      plsqlKeywords.add("MOD", (byte)7);
      plsqlKeywords.add("MONTHS_BETWEEN", (byte)7);
      plsqlKeywords.add("NEW_TIME", (byte)7);
      plsqlKeywords.add("NEXT_DAY", (byte)7);
      plsqlKeywords.add("NLSSORT", (byte)7);
      plsqlKeywords.add("NSL_INITCAP", (byte)7);
      plsqlKeywords.add("NLS_LOWER", (byte)7);
      plsqlKeywords.add("NLS_UPPER", (byte)7);
      plsqlKeywords.add("NVL", (byte)7);
      plsqlKeywords.add("POWER", (byte)7);
      plsqlKeywords.add("RAWTOHEX", (byte)7);
      plsqlKeywords.add("REPLACE", (byte)7);
      plsqlKeywords.add("ROUND", (byte)7);
      plsqlKeywords.add("ROWIDTOCHAR", (byte)7);
      plsqlKeywords.add("RPAD", (byte)7);
      plsqlKeywords.add("RTRIM", (byte)7);
      plsqlKeywords.add("SIGN", (byte)7);
      plsqlKeywords.add("SOUNDEX", (byte)7);
      plsqlKeywords.add("SIN", (byte)7);
      plsqlKeywords.add("SINH", (byte)7);
      plsqlKeywords.add("SQRT", (byte)7);
      plsqlKeywords.add("SUBSTR", (byte)7);
      plsqlKeywords.add("SUBSTRB", (byte)7);
      plsqlKeywords.add("TAN", (byte)7);
      plsqlKeywords.add("TANH", (byte)7);
      plsqlKeywords.add("TO_CHAR", (byte)7);
      plsqlKeywords.add("TO_DATE", (byte)7);
      plsqlKeywords.add("TO_MULTIBYTE", (byte)7);
      plsqlKeywords.add("TO_NUMBER", (byte)7);
      plsqlKeywords.add("TO_SINGLE_BYTE", (byte)7);
      plsqlKeywords.add("TRANSLATE", (byte)7);
      plsqlKeywords.add("TRUNC", (byte)7);
      plsqlKeywords.add("UPPER", (byte)7);
      plsqlKeywords.add("VERIFY", (byte)6);
      plsqlKeywords.add("SERVEROUTPUT", (byte)6);
      plsqlKeywords.add("PAGESIZE", (byte)6);
      plsqlKeywords.add("LINESIZE", (byte)6);
      plsqlKeywords.add("ARRAYSIZE", (byte)6);
      plsqlKeywords.add("DBMS_OUTPUT", (byte)6);
      plsqlKeywords.add("PUT_LINE", (byte)6);
      plsqlKeywords.add("ENABLE", (byte)6);
   }

   private static void addDataTypes() {
      plsqlKeywords.add("binary", (byte)6);
      plsqlKeywords.add("bit", (byte)6);
      plsqlKeywords.add("blob", (byte)6);
      plsqlKeywords.add("boolean", (byte)6);
      plsqlKeywords.add("char", (byte)6);
      plsqlKeywords.add("character", (byte)6);
      plsqlKeywords.add("DATE", (byte)6);
      plsqlKeywords.add("datetime", (byte)6);
      plsqlKeywords.add("DEC", (byte)6);
      plsqlKeywords.add("decimal", (byte)6);
      plsqlKeywords.add("DOUBLE PRECISION", (byte)6);
      plsqlKeywords.add("float", (byte)6);
      plsqlKeywords.add("image", (byte)6);
      plsqlKeywords.add("int", (byte)6);
      plsqlKeywords.add("integer", (byte)6);
      plsqlKeywords.add("money", (byte)6);
      plsqlKeywords.add("name", (byte)6);
      plsqlKeywords.add("NATURAL", (byte)6);
      plsqlKeywords.add("NATURALN", (byte)6);
      plsqlKeywords.add("NUMBER", (byte)6);
      plsqlKeywords.add("numeric", (byte)6);
      plsqlKeywords.add("nchar", (byte)6);
      plsqlKeywords.add("nvarchar", (byte)6);
      plsqlKeywords.add("ntext", (byte)6);
      plsqlKeywords.add("pls_integer", (byte)6);
      plsqlKeywords.add("POSITIVE", (byte)6);
      plsqlKeywords.add("POSITIVEN", (byte)6);
      plsqlKeywords.add("RAW", (byte)6);
      plsqlKeywords.add("real", (byte)6);
      plsqlKeywords.add("ROWID", (byte)6);
      plsqlKeywords.add("SIGNTYPE", (byte)6);
      plsqlKeywords.add("smalldatetime", (byte)6);
      plsqlKeywords.add("smallint", (byte)6);
      plsqlKeywords.add("smallmoney", (byte)6);
      plsqlKeywords.add("text", (byte)6);
      plsqlKeywords.add("timestamp", (byte)6);
      plsqlKeywords.add("tinyint", (byte)6);
      plsqlKeywords.add("uniqueidentifier", (byte)6);
      plsqlKeywords.add("UROWID", (byte)6);
      plsqlKeywords.add("varbinary", (byte)6);
      plsqlKeywords.add("varchar", (byte)6);
      plsqlKeywords.add("varchar2", (byte)6);
   }

   private static void addSystemFunctions() {
      plsqlKeywords.add("SYSDATE", (byte)7);
   }

   private static void addOperators() {
      plsqlKeywords.add("ALL", (byte)9);
      plsqlKeywords.add("AND", (byte)9);
      plsqlKeywords.add("ANY", (byte)9);
      plsqlKeywords.add("BETWEEN", (byte)9);
      plsqlKeywords.add("BY", (byte)9);
      plsqlKeywords.add("CONNECT", (byte)9);
      plsqlKeywords.add("EXISTS", (byte)9);
      plsqlKeywords.add("IN", (byte)9);
      plsqlKeywords.add("INTERSECT", (byte)9);
      plsqlKeywords.add("LIKE", (byte)9);
      plsqlKeywords.add("NOT", (byte)9);
      plsqlKeywords.add("NULL", (byte)9);
      plsqlKeywords.add("OR", (byte)9);
      plsqlKeywords.add("START", (byte)9);
      plsqlKeywords.add("UNION", (byte)9);
      plsqlKeywords.add("WITH", (byte)9);
   }

   private static void addSystemStoredProcedures() {
      plsqlKeywords.add("sp_add_agent_parameter", (byte)8);
   }

   private static void addSystemTables() {
      plsqlKeywords.add("backupfile", (byte)8);
   }
}
