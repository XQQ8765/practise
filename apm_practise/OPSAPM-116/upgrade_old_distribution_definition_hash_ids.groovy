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

def getNewDistributionHashId = {
	def definition = NameRegistry.getObject("apm.common.distribution.RESPONSE_TIME_DISTRIBUTION")
	return definition?.distributionHashId
}

def getObservationalTables = {
	def tables = []
	def config = storageMgrSvc.storageConfiguration;
	def observationsStorages = config.getStorageTypes().findAll { obsStorage ->
		return obsStorage.storageType == "Distribution"
	}
	observationsStorages?.each { observationStorage ->
		observationStorage?.getTables()?.each { table ->
			if ( ObservationTable.State.ASSIGNED.equals(table.getState())  ||
					ObservationTable.State.UNASSIGNED.equals(table.getState()) ) {
				tables << table
			}
		}
	}
	tables
}

def getOldDistributionHashId = {
	def oldDistributionHash = "e3bbdad2c5ca09366878f52a127257ed"//The hash value for old difinition "apm.monitor.common.RESPONSE_TIME_DISTRIBUTION"
	def querySQL = "SELECT dh_id FROM distribution_hash WHERE dh_distribution_hash=?".toString()
	def row = sql.firstRow(querySQL, [oldDistributionHash])
	return row?.dh_id
}

def updateDistributionHashID = { tables, newDistributionHashId, oldDistributionHashId ->
	tables?.each { table ->
		def updateSqlText = "UPDATE ${table.name} SET od_distribution_hash_id = ${newDistributionHashId} WHERE od_distribution_hash_id = ${oldDistributionHashId}".toString()
		def result = sql.executeUpdate(updateSqlText)
		log "Executing update SQL: ${updateSqlText}, updated ${result} rows."
	}
}

def createTriggers = {
	def oldDistributionHashId = getOldDistributionHashId()
	if (oldDistributionHashId == null) {
		log "Can not find the hashId for definition: \'apm.common.distribution.RESPONSE_TIME_DISTRIBUTION\'."
		return
	} else {
		log "oldDistributionHashId:${oldDistributionHashId}."
	}
	def newDistributionHashId = getNewDistributionHashId()
	if (newDistributionHashId == null) {
		log "Can not find the new distribution definition: \'apm.common.distribution.RESPONSE_TIME_DISTRIBUTION\'."
		return
	} else {
		log "newDistributionHashId:${newDistributionHashId}."
	}
	def tables = getObservationalTables()
	if (isCollectionBlank(tables)) {
		log "Observation tables are blank."
		return
	}
	updateDistributionHashID(tables, newDistributionHashId, oldDistributionHashId)
	log "Finished update distribution hash IDs."
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

