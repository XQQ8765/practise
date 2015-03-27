import javax.management.ObjectName;
import com.quest.nitro.service.util.*;
import com.quest.nitro.service.persistence.obs.config.*;
import com.quest.nitro.service.persistence.obs.storage.*;
import com.quest.nitro.service.persistence.obs.storage.StorageManagerService.ConfigUpdateTask;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import groovy.sql.*;
import java.sql.Timestamp;
import com.quest.forge.data.*;

def storageMgrSvc = new MBeanRef(ObjectName.getInstance("com.quest.nitro:service=StorageManager")).ref()
def ds = JDBCHelper.getDataSource(null)
def sql = new Sql(ds)
def output = new StringBuilder()
def moveLobScript = new StringBuilder()

def log = { msg ->
	println msg;  // write to logs
	output << msg << '\n'; // add to script output
}

def toTimeStamp = { timestampStr ->
	return new Timestamp(timestampStr)
}

def isCollectionBlank = { collection ->
	(collection == null) || (collection.size() == 0)
} 

def getDistributionHashIds = {
	def querySQL = "SELECT * FROM distribution_hash".toString()
	sql.eachRow(querySQL, { row ->
		log "distribution_hash row: ${row}"
	})
}

def getNewDistributionHashId = {
	def definition = NameRegistry.getObject("apm.common.distribution.RESPONSE_TIME_DISTRIBUTION")
	return definition?.distributionHashId
}

def createTriggers = {
	getDistributionHashIds()
	def newDistributionHashId = getNewDistributionHashId()
	if (newDistributionHashId == null) {
		log "Can not find the new distribution definition: \'apm.common.distribution.RESPONSE_TIME_DISTRIBUTION\'."
		return
	} else {
		log "newDistributionHashId:${newDistributionHashId}."
	}
}

try {
	createTriggers()
}
catch (Throwable t) {
	def sw = new StringWriter();
	sw.append("Failed to update distribution hash IDs:\n")
	t.printStackTrace(new PrintWriter(sw))
	log sw.toString()
}

return output.toString()

