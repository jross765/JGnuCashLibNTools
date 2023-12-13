package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorJobImpl;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileJobManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager.class);
    
    // ---------------------------------------------------------------
    
    protected GnucashFileImpl gcshFile;

    private Map<GCshID, GnucashGenerJob>    jobMap;
    private Map<GCshID, GnucashCustomerJob> custJobMap;
    private Map<GCshID, GnucashVendorJob>   vendJobMap;

    // ---------------------------------------------------------------
    
    public FileJobManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
        jobMap     = new HashMap<GCshID, GnucashGenerJob>();
        custJobMap = new HashMap<GCshID, GnucashCustomerJob>();
        vendJobMap = new HashMap<GCshID, GnucashVendorJob>();
    
        for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
            Object bookElement = iter.next();
            if (!(bookElement instanceof GncV2.GncBook.GncGncJob)) {
        	continue;
            }
            GncV2.GncBook.GncGncJob jwsdpJob = (GncV2.GncBook.GncGncJob) bookElement;
    
            try {
        	GnucashGenerJobImpl generJob = createGenerJob(jwsdpJob);
        	GCshID jobID = generJob.getID();
        	if (jobID == null) {
        	    LOGGER.error("init: File contains a (generic) Job w/o an ID. indexing it with the Null-ID '" + GCshID.NULL_ID + "'");
        	    jobID = new GCshID(GCshID.NULL_ID);
        	}
        	jobMap.put(jobID, generJob);
        	
        	if ( generJob.getOwnerType() == GnucashGenerJob.TYPE_CUSTOMER ) {
        	    GnucashCustomerJobImpl custJob = createCustomerJob(jwsdpJob);
        	    GCshID custJobID = custJob.getID();
        	    if (custJobID == null) {
        		LOGGER.error("init: File contains a customer Job w/o an ID. indexing it with the Null-ID '" + GCshID.NULL_ID + "'");
        		custJobID = new GCshID(GCshID.NULL_ID);
        	    }
        	    custJobMap.put(custJobID, custJob);
        	} else if ( generJob.getOwnerType() == GnucashGenerJob.TYPE_VENDOR ) {
        	    GnucashVendorJobImpl vendJob = createVendorJob(jwsdpJob);
        	    GCshID vendJobID = vendJob.getID();
        	    if (vendJobID == null) {
        		LOGGER.error("init: File contains a vendor Job w/o an ID. indexing it with the Null-ID '" + GCshID.NULL_ID + "'");
        		vendJobID = new GCshID(GCshID.NULL_ID);
        	    }
        	    vendJobMap.put(vendJobID, vendJob);
        	}
        	
            } catch (RuntimeException e) {
        	LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
        		+ "ignoring illegal (generic) Job entry with id=" + jwsdpJob.getJobId(), e);
            }
        } // for
    
        LOGGER.debug("init: No. of entries in generic Job map: " + jobMap.size());
        LOGGER.debug("init: No. of entries in customer Job map: " + custJobMap.size());
        LOGGER.debug("init: No. of entries in vendor Job map: " + vendJobMap.size());
	
	if ( jobMap.size() != custJobMap.size() + vendJobMap.size() ) {
	    LOGGER.error("init: Numbers of entries in three map objects are not consistent");
	}
    }

    /**
     * @param jwsdpJob the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashJob to wrap the given jaxb-object.
     */
    protected GnucashGenerJobImpl createGenerJob(final GncV2.GncBook.GncGncJob jwsdpJob) {
	GnucashGenerJobImpl job = new GnucashGenerJobImpl(jwsdpJob, gcshFile);
	LOGGER.info("Generated new generic job: " + job.getID());
	return job;
    }

    protected GnucashCustomerJobImpl createCustomerJob(final GncV2.GncBook.GncGncJob jwsdpJob) {
	GnucashCustomerJobImpl job = new GnucashCustomerJobImpl(jwsdpJob, gcshFile);
	LOGGER.info("Generated new customer job: " + job.getID());
	return job;
    }

    protected GnucashVendorJobImpl createVendorJob(final GncV2.GncBook.GncGncJob jwsdpJob) {
	GnucashVendorJobImpl job = new GnucashVendorJobImpl(jwsdpJob, gcshFile);
	LOGGER.info("Generated new vendor job: " + job.getID());
	return job;
    }

    // ---------------------------------------------------------------

    public void addGenerJob(GnucashGenerJob job) {
	addGenerJob(job, true);
	LOGGER.debug("Added (generic) jop to cache: " + job.getID());
    }

    private void addGenerJob(GnucashGenerJob job, boolean withSpec) {
	jobMap.put(job.getID(), job);
	
	if ( withSpec ) {
	    if ( job.getOwnerType() == GnucashGenerJob.TYPE_CUSTOMER ) {
		addCustomerJob((GnucashCustomerJob) job, false);
	    } else if ( job.getOwnerType() == GnucashGenerJob.TYPE_VENDOR ) {
		addVendorJob((GnucashVendorJob) job, false);
	    }
	}

	LOGGER.debug("addGenerJob: No. of generic jobs: " + jobMap.size());
	LOGGER.debug("addGenerJob: No. of customer jobs: " + custJobMap.size());
	LOGGER.debug("addGenerJob: No. of vendor jobs: " + vendJobMap.size());
	
	if ( jobMap.size() != custJobMap.size() + vendJobMap.size() ) {
	    LOGGER.error("addGenerJob: Numbers of entries in three map objects are not consistent");
	}
	
	LOGGER.debug("Removed (generic) jop from cache: " + job.getID());
    }

    public void removeGenerJob(GnucashGenerJob job) {
	removeGenerJob(job, true);
    }

    private void removeGenerJob(GnucashGenerJob job, boolean withSpec) {
	jobMap.remove(job.getID());
	
	if ( withSpec ) {
	    if ( job.getOwnerType() == GnucashGenerJob.TYPE_CUSTOMER ) {
		addCustomerJob((GnucashCustomerJob) job, false);
	    } else if ( job.getOwnerType() == GnucashGenerJob.TYPE_VENDOR ) {
		addVendorJob((GnucashVendorJob) job, false);
	    }
	}

	LOGGER.debug("removeGenerJob: No. of generic jobs: " + jobMap.size());
	LOGGER.debug("removeGenerJob: No. of customer jobs: " + custJobMap.size());
	LOGGER.debug("removeGenerJob: No. of vendor jobs: " + vendJobMap.size());
	
	if ( jobMap.size() != custJobMap.size() + vendJobMap.size() ) {
	    LOGGER.error("removeGenerJob: Numbers of entries in three map objects are not consistent");
	}
    }

    // ----------------------------

    public void addCustomerJob(GnucashCustomerJob job) {
	addCustomerJob(job, true);
    }

    private void addCustomerJob(GnucashCustomerJob job, boolean withGener) {
	custJobMap.put(job.getID(), job);
	
	if ( withGener ) {
	    addGenerJob(job, false);
	}

	LOGGER.debug("addCustomerJob: No. of generic jobs: " + jobMap.size());
	LOGGER.debug("addCustomerJob: No. of customer jobs: " + custJobMap.size());
	LOGGER.debug("addCustomerJob: No. of vendor jobs: " + vendJobMap.size());
	
	if ( jobMap.size() != custJobMap.size() + vendJobMap.size() ) {
	    LOGGER.error("addCustomerJob: Numbers of entries in three map objects are not consistent");
	}
    }

    public void removeCustomerJob(GnucashCustomerJob job) {
	removeCustomerJob(job, true);
    }

    private void removeCustomerJob(GnucashCustomerJob job, boolean withGener) {
	custJobMap.remove(job.getID());
	
	if ( withGener ) {
	    removeGenerJob(job, false);
	}

	LOGGER.debug("removeCustomerJob: No. of generic jobs: " + jobMap.size());
	LOGGER.debug("removeCustomerJob: No. of customer jobs: " + custJobMap.size());
	LOGGER.debug("removeCustomerJob: No. of vendor jobs: " + vendJobMap.size());
	
	if ( jobMap.size() != custJobMap.size() + vendJobMap.size() ) {
	    LOGGER.error("removeCustomerJob: Numbers of entries in three map objects are not consistent");
	}
    }

    // ----------------------------

    public void addVendorJob(GnucashVendorJob job) {
	addVendorJob(job, true);
    }

    public void addVendorJob(GnucashVendorJob job, boolean withGener) {
	vendJobMap.put(job.getID(), job);
	
	if ( withGener ) {
	    addGenerJob(job, false);
	}

	LOGGER.debug("addVendorJob: No. of generic jobs: " + jobMap.size());
	LOGGER.debug("addVendorJob: No. of customer jobs: " + custJobMap.size());
	LOGGER.debug("addVendorJob: No. of vendor jobs: " + vendJobMap.size());
	
	if ( jobMap.size() != custJobMap.size() + vendJobMap.size() ) {
	    LOGGER.error("addVendorJob: Numbers of entries in three map objects are not consistent");
	}
    }

    public void removeVendorJob(GnucashVendorJob job) {
	removeVendorJob(job, true);
    }

    public void removeVendorJob(GnucashVendorJob job, boolean withGener) {
	vendJobMap.remove(job.getID());
	
	if ( withGener ) {
	    removeGenerJob(job, false);
	}
	
	LOGGER.debug("removeVendorJob: No. of generic jobs: " + jobMap.size());
	LOGGER.debug("removeVendorJob: No. of customer jobs: " + custJobMap.size());
	LOGGER.debug("removeVendorJob: No. of vendor jobs: " + vendJobMap.size());
	
	if ( jobMap.size() != custJobMap.size() + vendJobMap.size() ) {
	    LOGGER.error("removeVendorJob: Numbers of entries in three map objects are not consistent");
	}
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getGenerJobByID(java.lang.String)
     */
    public GnucashGenerJob getGenerJobByID(final GCshID id) {
	if (jobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashGenerJob retval = jobMap.get(id);
	if (retval == null) {
	    LOGGER.warn("getGenerJobByID: No generic Job with ID '" + id + "'. We know " + jobMap.size() + " jobs.");
	}

	return retval;
    }

    public Collection<GnucashGenerJob> getGenerJobsByName(String name) {
	return getGenerJobsByName(name, true);
    }
    
    public Collection<GnucashGenerJob> getGenerJobsByName(final String expr, final boolean relaxed) {
	if (jobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<GnucashGenerJob> result = new ArrayList<GnucashGenerJob>();
	
	for ( GnucashGenerJob job : jobMap.values() ) {
	    if ( relaxed ) {
		if ( job.getName().trim().toLowerCase().
			contains(expr.trim().toLowerCase()) ) {
		    result.add(job);
		}
	    } else {
		if ( job.getName().equals(expr) ) {
		    result.add(job);
		}
	    }
	}

	return result;
    }
    
    public GnucashGenerJob getGenerJobByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<GnucashGenerJob> jobList = getGenerJobsByName(name, false);
	if ( jobList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( jobList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return jobList.iterator().next();
    }
    
    /**
     * @see GnucashFile#getGenerJobs()
     */
    public Collection<GnucashGenerJob> getGenerJobs() {
	if (jobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return jobMap.values();
    }

    // ----------------------------

    /**
     * @see GnucashFile#getGenerJobByID(java.lang.String)
     */
    public GnucashCustomerJob getCustomerJobByID(final GCshID id) {
	if (custJobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashCustomerJob retval = custJobMap.get(id);
	if (retval == null) {
	    LOGGER.warn("getCustomerJobByID: No customer Job with ID '" + id + "'. We know " + custJobMap.size() + " jobs.");
	}

	return retval;
    }

    public Collection<GnucashCustomerJob> getCustomerJobsByName(String name) {
	return getCustomerJobsByName(name, true);
    }
    
    public Collection<GnucashCustomerJob> getCustomerJobsByName(final String expr, final boolean relaxed) {
	if (custJobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<GnucashCustomerJob> result = new ArrayList<GnucashCustomerJob>();
	
	for ( GnucashCustomerJob job : custJobMap.values() ) {
	    if ( relaxed ) {
		if ( job.getName().trim().toLowerCase().
			contains(expr.trim().toLowerCase()) ) {
		    result.add(job);
		}
	    } else {
		if ( job.getName().equals(expr) ) {
		    result.add(job);
		}
	    }
	}

	return result;
    }
    
    public GnucashCustomerJob getCustomerJobByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<GnucashCustomerJob> jobList = getCustomerJobsByName(name, false);
	if ( jobList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( jobList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return jobList.iterator().next();
    }
    
    /**
     * @see GnucashFile#getGenerJobs()
     */
    public Collection<GnucashCustomerJob> getCustomerJobs() {
	if (custJobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return custJobMap.values();
    }

    // ----------------------------

    /**
     * @see GnucashFile#getGenerJobByID(java.lang.String)
     */
    public GnucashVendorJob getVendorJobByID(final GCshID id) {
	if (vendJobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashVendorJob retval = vendJobMap.get(id);
	if (retval == null) {
	    LOGGER.warn("getVendorJobByID: No vendor Job with ID '" + id + "'. We know " + vendJobMap.size() + " jobs.");
	}

	return retval;
    }

    public Collection<GnucashVendorJob> getVendorJobsByName(String name) {
	return getVendorJobsByName(name, true);
    }
    
    public Collection<GnucashVendorJob> getVendorJobsByName(final String expr, final boolean relaxed) {
	if (vendJobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<GnucashVendorJob> result = new ArrayList<GnucashVendorJob>();
	
	for ( GnucashVendorJob job : vendJobMap.values() ) {
	    if ( relaxed ) {
		if ( job.getName().trim().toLowerCase().
			contains(expr.trim().toLowerCase()) ) {
		    result.add(job);
		}
	    } else {
		if ( job.getName().equals(expr) ) {
		    result.add(job);
		}
	    }
	}

	return result;
    }
    
    public GnucashVendorJob getVendorJobByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<GnucashVendorJob> jobList = getVendorJobsByName(name, false);
	if ( jobList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( jobList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return jobList.iterator().next();
    }
    
    /**
     * @see GnucashFile#getGenerJobs()
     */
    public Collection<GnucashVendorJob> getVendorJobs() {
	if (vendJobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return vendJobMap.values();
    }

    // ---------------------------------------------------------------

    /**
     * @param cust the customer to look for.
     * @return all jobs that have this customer, never null
     */
    public Collection<GnucashCustomerJob> getJobsByCustomer(final GnucashCustomer cust) {
	if (custJobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<GnucashCustomerJob> retval = new LinkedList<GnucashCustomerJob>();

	for (GnucashCustomerJob custJob : custJobMap.values()) {
	    if (custJob.getOwnerID().equals(cust.getID())) {
		retval.add(custJob);
	    }
	}
	
	return retval;
    }

    /**
     * @param vend the vendor to look for.
     * @return all jobs that have this vendor, never null
     */
    public Collection<GnucashVendorJob> getJobsByVendor(final GnucashVendor vend) {
	if (vendJobMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<GnucashVendorJob> retval = new LinkedList<GnucashVendorJob>();

	for (GnucashVendorJob vendJob : vendJobMap.values()) {
	    if (vendJob.getOwnerID().equals(vend.getID())) {
		retval.add(vendJob);
	    }
	}
	
	return retval;
    }

    // ---------------------------------------------------------------

    public int getNofEntriesGenerJobMap() {
	return jobMap.size();
    }

    public int getNofEntriesCustomerJobMap() {
	return custJobMap.size();
    }

    public int getNofEntriesVendorJobMap() {
	return vendJobMap.size();
    }

}
