#echo "runing update"
cd /home/kali/IdeaProjects/JMCoin

git stash >> /dev/null
git fetch >> /dev/null

CURRENTCOMMITNUMBER=$(git rev-list --count origin/dev)
LOCALCOMMITNUMBER=$(cat /home/kali/Desktop/commits.txt)

#echo "curent" $CURRENTCOMMITNUMBER
#echo "local" $LOCALCOMMITNUMBER

BLUE='\033[1;34m'
NC='\033[0m' # No Color

if [ "$((LOCALCOMMITNUMBER))" -lt "$((CURRENTCOMMITNUMBER))" ]
then
	echo "${BLUE}[SCRIPT UPDATE INFO] Seems to have a new commit..${NC}"
	echo $((CURRENTCOMMITNUMBER)) > /home/kali/Desktop/commits.txt

	rm -rf /home/kali/IdeaProjects/JMCoin
	cd /home/kali/IdeaProjects/
	git clone ssh://git@github.com/NicodemeB/JMCoin.git --branch=dev
	cd /home/kali/IdeaProjects/JMCoin
	mvn package

	if [ $(ps -ax |grep 'TestMasterNode' | awk '{print $1}' |grep -c "") -gt 0 ]
        then
		echo "${BLUE}[SCRIPT UPDATE INFO] Killing old java ...${NC}"
		sudo kill $(ps -ax |grep 'TestMasterNode' | awk '{print $1}')
	fi

	echo "${BLUE}[SCRIPT UPDATE INFO] Editing persistence.xml${NC}"
	cp /home/kali/Desktop/persistence.xml /home/kali/IdeaProjects/JMCoin/src/main/resources/META-INF/persistence.xml

	echo "${BLUE}[SCRIPT UPDATE INFO] Running new commit ...${NC}"
	mvn exec:java -Dexec.mainClass="com.jmcoin.test.TestMasterNode"
#else
#	echo "${BLUE}[SCRIPT UPDATE INFO] Seems to NOT have a new commit..${NC}"
fi