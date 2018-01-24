SET VERSION=%1
SET SCRIPTS_DIR=%2
SET BASE=base version.1.11.0.sql

psql -U postgres -d postgres -a -f "%SCRIPTS_DIR%\%BASE%" -v "ON_ERROR_STOP=1"
if ERRORLEVEL 1 (
	ECHO Fail. Check sql file
	EXIT /B 1
)

FOR /L %%J in (12,1,%VERSION%) DO (
	FOR %%I in (%SCRIPTS_DIR%\patches_to_v1.%%J\*.sql) DO (
		psql -U postgres -d postgres -a -f %%I -v "ON_ERROR_STOP=1"
		if ERRORLEVEL 1 (
			ECHO Fail. Check sql file
			EXIT /B 1
		)
	)
)
