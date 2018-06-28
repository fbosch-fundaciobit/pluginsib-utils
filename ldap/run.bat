
@ECHO OFF
CLS
:MENU
ECHO.
ECHO ...............................................
ECHO Escriu 0, 1 o 2 i pitja ENTER:
ECHO ...............................................
ECHO.
ECHO 0 - Sortir
ECHO 1 - Executa TesterLDAPApp
ECHO.
SET /P M=Escriu 0 o 1 i pitja ENTER:
IF %M%==0 GOTO EOF
IF %M%==1 GOTO EXAMPLE

GOTO EOF

:EXAMPLE

mvn exec:java -Dexec.mainClass="org.fundaciobit.plugins.utils.ldap.TesterLdapApp" -Dexec.args="%1 %2 %3"

GOTO EOF




EOF:

