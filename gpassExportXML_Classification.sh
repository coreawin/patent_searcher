# ========================================================================
# G-PASS 검색 Export 모듈 [홈 디렉토리]
#-au, Authority
#-tg, target export xml Path(다운로드 경로)
#-ct, XML을 다운로드 받은이후 (XML에서 필수)
#-cn, Per Result Count
#-ef, Export 대상 필드
# ft, Export 대상 타입 (XML, EXCEL) - to
#-ke, Keyword Extract Home 디렉토리 경로 - > EX)e:\keyword_extract\data (x) ==> e:\keyword_extract
# ========================================================================
HOME=./
GPASS_EXPORT_XML=$HOME

LIB=.
for i in 'ls ${HOME}lib/*.jar'
do
  LIB=${LIB}:${i}
done

RESUTL_COUNT=10000
AUTHORITY=JP,US,KR,DE
TARGET_DIR=/data/home/*****/
COMPRESS_TARGET_FILE=/data/home/****/
WORK_DIR=$GPASS_EXPORT_XML/work/
EXPORT_TYPE=EXCEL
EXPORT_FIELD=PNO,TI,ABS
SEARCH_RULE="(OLED).ti.abs.\ dddd\ dddd\ dddd"
KEYWORD_DIC_PATH=/data/home/****/

nohup java -server -Xms1g -Xmx4g -classpath $LIB com.diquest.Executor -au$AUTHORITY -tg$TARGET_DIR -ru"$SEARCH_RULE" -ft$EXPORT_TYPE -ct$COMPRESS_TARGET_FILE -cn$RESUTL_COUNT -ef$EXPORT_FIELD -ke$KEYWORD_DIC_PATH &

