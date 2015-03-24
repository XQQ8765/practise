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
import com.quest.nitro.service.sl.interfaces.security.tenant.*;

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
	def storageMgrSvc = new MBeanRef(ObjectName.getInstance("com.quest.nitro:service=StorageManager")).ref()
	def config = storageMgrSvc.storageConfiguration;
	def observationsStorages = config.getStorageTypes().findAll { obsStorage ->
		return obsStorage.storageType == "Distribution"
	}

	def tables = []
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

def getOldDistributionHashId = { sql ->
	def oldDistributionHash = "e3bbdad2c5ca09366878f52a127257ed"//The hash value for old difinition "apm.monitor.common.RESPONSE_TIME_DISTRIBUTION"
	def querySQL = "SELECT dh_id FROM distribution_hash WHERE dh_distribution_hash=?".toString()
	def row = sql.firstRow(querySQL, [oldDistributionHash])
	return row?.dh_id
}

def updateDistributionHashID = { sql, tables, newDistributionHashId, oldDistributionHashId ->
	tables?.each { table ->
		def updateSqlText = "UPDATE ${table.name} SET od_distribution_hash_id = ${newDistributionHashId} WHERE od_distribution_hash_id = ${oldDistributionHashId}".toString()
		def result = sql.executeUpdate(updateSqlText)
		log "Executing update SQL: ${updateSqlText}, updated ${result} rows."
	}
}

def createTriggers = { tenant ->
	def ds = JDBCHelper.getDataSource(null)
	def sql = new Sql(ds)

	def oldDistributionHashId = getOldDistributionHashId(sql)
	if (oldDistributionHashId == null) {
		log "Can not find the hashId for definition: \'apm.monitor.common.RESPONSE_TIME_DISTRIBUTION\'."
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
	updateDistributionHashID(sql, tables, newDistributionHashId, oldDistributionHashId)
}

try {
	tenantManager = server.TenantManager;
	tenants = tenantManager.getAllTenants();
	tenants?.each { tenant ->
		tenantManager.runAs(tenant, { 	
    		def tenantDetail = "Tenant ID=${tenant.getId()}, Tenant uniqueID=${tenant.getUniqueId()}, Tenant name=${tenant.getName()}".toString()
			log "\n\n"
			log "Start to update hashIDs on: ${tenantDetail}"

			createTriggers(tenant)

			log "Finishing update hashIDs on: ${tenantDetail}"
		} as java.util.concurrent.Callable)
	}
}
catch (Throwable t) {
	def sw = new StringWriter();
	sw.append("Failed to update distribution hash IDs:\n")
	t.printStackTrace(new PrintWriter(sw))
	log sw.toString()
}

return output.toString()
