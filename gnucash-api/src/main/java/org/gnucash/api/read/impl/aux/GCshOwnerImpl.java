package org.gnucash.api.read.impl.aux;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.MappingException;

public class GCshOwnerImpl implements GCshOwner {
	
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshOwnerImpl.class);

	// ---------------------------------------------------------------

    /**
     * The JWSDP-object we are wrapping.
     */
//    private GncGncInvoice.InvoiceOwner jwsdpInvcOwner;
//    private GncGncJob.JobOwner         jwsdpJobOwner;

    /**
     * the file we belong to.
     */
    protected final GnuCashFile myFile;
    
    // ----------------------------
    
    private JIType          jiType   = null;
    private Type            invcType = null;
    private Type            jobType  = null;
    private GCshID          ownerID  = null;
    
    private GnuCashCustomer cust = null;
    private GnuCashVendor   vend = null;
    private GnuCashEmployee empl = null;
    private GnuCashGenerJob job  = null;

	// ---------------------------------------------------------------

    @SuppressWarnings("exports")
	public GCshOwnerImpl(
	    final GncGncInvoice.InvoiceOwner jwsdpInvcOwner,
	    final GnuCashFile gcshFile) {
		this.myFile = gcshFile;

		this.jiType = JIType.INVOICE;
		mapInvcType_Var1(jwsdpInvcOwner.getOwnerId());
		this.ownerID = new GCshID(jwsdpInvcOwner.getOwnerId().getValue());
		
		mapObject();
    }

    @SuppressWarnings("exports")
	public GCshOwnerImpl(
	    final GncGncJob.JobOwner jwsdpJobOwner,
	    final GnuCashFile gcshFile) {
		this.myFile = gcshFile;

		this.jiType = JIType.JOB;
		mapJobType_Var1(jwsdpJobOwner.getOwnerId());
		this.ownerID = new GCshID(jwsdpJobOwner.getOwnerId().getValue());
		
		mapObject();
    }

    @SuppressWarnings("exports")
	public GCshOwnerImpl(
	    final org.gnucash.api.generated.OwnerId ownerID,
	    final GnuCashFile gcshFile) {
		this.myFile = gcshFile;
		
		mapJIType_Var1(ownerID);
		if ( jiType == JIType.INVOICE )
			mapInvcType_Var1(ownerID);
		else if ( jiType == JIType.JOB )
			mapJobType_Var1(ownerID);
		this.ownerID = new GCshID(ownerID.getValue());
		
		mapObject();
    }

    public GCshOwnerImpl(
    	    final GCshID ownerID, JIType jiType,
    	    final GnuCashFile gcshFile) {
    	this.myFile = gcshFile;
    	
    	this.ownerID = ownerID; // Caution: In this case, *before* type mapping
		this.jiType = jiType;
		if ( jiType == JIType.INVOICE )
			mapInvcType_Var2();
		else if ( jiType == JIType.JOB )
			mapJobType_Var2();
		
		mapObject();
    }

	// ---------------------------------------------------------------
    
    private void mapJIType_Var1(org.gnucash.api.generated.OwnerId ownerID) {
    	try {
    		job = myFile.getGenerJobByID(new GCshID(ownerID.getValue()));
    		jiType = JIType.JOB;
//    		jwsdpInvcOwner = null;
//    		jwsdpJobOwner.setOwnerId(ownerID);
    	} catch ( Exception exc ) {
    		jiType = JIType.INVOICE;
//    		jwsdpInvcOwner.setOwnerId(ownerID);
//    		jwsdpJobOwner = null;
    	}
    }
    
//    private void mapJIType_Var2() {
//    	// ::TODO
//    }
    
    // ----------------------------
    
    private void mapInvcType_Var1(org.gnucash.api.generated.OwnerId jwsdpOwnerID) {
    	if ( jiType != JIType.INVOICE )
    		throw new IllegalStateException("Wrong JI type");
    	
		invcType = Type.valueOff(jwsdpOwnerID.getType());
    }
    
    private void mapInvcType_Var2() {
    	try {
    		cust = myFile.getCustomerByID(ownerID);
    		if ( cust != null )
    			invcType = GnuCashGenerInvoice.TYPE_CUSTOMER;
    		else {
        		vend = myFile.getVendorByID(ownerID);
        		if ( vend != null )
        			invcType = GnuCashGenerInvoice.TYPE_VENDOR;
        		else {
            		empl = myFile.getEmployeeByID(ownerID);
            		if ( empl != null )
            			invcType = GnuCashGenerInvoice.TYPE_EMPLOYEE;
            		else {
                		job = myFile.getGenerJobByID(ownerID);
                		if ( job != null )
                			invcType = GnuCashGenerInvoice.TYPE_JOB;
                		else
                    		throw new MappingException("Owner ID '" + ownerID + "' cannot be mapped to owner type");
            		}
        		}
    		}
    	} catch ( Exception exc ) {
    		throw new MappingException("Owner ID '" + ownerID + "' cannot be mapped to owner type");
    	}
    }
    
    // ----------------------------
    
    private void mapJobType_Var1(org.gnucash.api.generated.OwnerId jwsdpOwnerID) {
    	if ( jiType != JIType.JOB )
    		throw new IllegalStateException("Wrong JI type");
    	
		jobType = Type.valueOff(jwsdpOwnerID.getType());
		
		if ( jobType != GnuCashGenerJob.TYPE_CUSTOMER &&
			 jobType != GnuCashGenerJob.TYPE_VENDOR ) {
			throw new MappingException("Job type could not be mapped correctly");
		}
    }
    
    private void mapJobType_Var2() {
    	try {
    		cust = myFile.getCustomerByID(ownerID);
    		if ( cust != null )
    			jobType = GnuCashGenerJob.TYPE_CUSTOMER;
    		else {
        		vend = myFile.getVendorByID(ownerID);
        		if ( vend != null )
        			jobType = GnuCashGenerJob.TYPE_VENDOR;
        		else {
            		throw new MappingException("Owner ID '" + ownerID + "' cannot be mapped to owner type");
        		}
    		}
    	} catch ( Exception exc1 ) {
    		throw new MappingException("Owner ID '" + ownerID + "' cannot be mapped to owner type");
    	}
    }
    
    // ----------------------------
    
    // Partially redundant to mapType_Var2
    private void mapObject() {
    	if ( jiType == JIType.INVOICE ) {
        	try {
        		if ( invcType == GnuCashGenerInvoice.TYPE_CUSTOMER ) {
        			cust = myFile.getCustomerByID(ownerID);
        			vend = null;
        			empl = null;
        			job  = null;
        		}
        		else if ( invcType == GnuCashGenerInvoice.TYPE_VENDOR ) {
        			cust = null;
        			vend = myFile.getVendorByID(ownerID);
        			empl = null;
        			job  = null;
        		}
        		else if ( invcType == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
        			cust = null;
        			vend = null;
        			empl = myFile.getEmployeeByID(ownerID);
        			job  = null;
        		}
        		else if ( invcType == GnuCashGenerInvoice.TYPE_JOB ) {
        			cust = null;
        			vend = null;
        			empl = null;
        			job = myFile.getGenerJobByID(ownerID);
        		}
        	} catch ( Exception exc ) {
        		throw new MappingException("Owner ID '" + ownerID + "' cannot be mapped to API object");
        	}
    	}
    	else if ( jiType == JIType.JOB ) {
    		if ( jobType == GnuCashGenerJob.TYPE_CUSTOMER ) {
    			cust = myFile.getCustomerByID(ownerID);
    			vend = null;
    			empl = null;
    			job  = null;
    		}
    		else if ( jobType ==  GnuCashGenerJob.TYPE_VENDOR ) {
    			cust = null;
    			vend = myFile.getVendorByID(ownerID);
    			empl = null;
    			job  = null;
    		}
    	}
    }

	// ---------------------------------------------------------------

	@Override
	public JIType getJIType() {
		return jiType;
	}

	@Override
	public Type getInvcType() {
		if ( jiType != JIType.INVOICE )
			throw new IllegalStateException("Wrong JI type");
		
		return invcType;
	}

	@Override
	public Type getJobType() {
		if ( jiType != JIType.JOB )
			throw new IllegalStateException("Wrong JI type");
		
		return jobType;
	}

	@Override
	public GCshID getID() {
		return ownerID;
	}

    // -----------------------------------------------------------------
    
    public GnuCashCustomer    getCustomer() {
    	if ( ( jiType == JIType.INVOICE &&
    		   invcType == GnuCashGenerInvoice.TYPE_CUSTOMER ) ||
    		 ( jiType == JIType.JOB &&
     		   jobType == GnuCashGenerJob.TYPE_CUSTOMER ) ) {
    		return myFile.getCustomerByID(ownerID);
    	} else {
    		throw new UnsupportedOperationException("Owner ID does not belong to customer");
    	}
    }

    public GnuCashVendor      getVendor() {
    	if ( ( jiType == JIType.INVOICE &&
     		   invcType == GnuCashGenerInvoice.TYPE_VENDOR ) ||
     		 ( jiType == JIType.JOB &&
      		   jobType == GnuCashGenerJob.TYPE_VENDOR ) ) {
     		return myFile.getVendorByID(ownerID);
     	} else {
     		throw new UnsupportedOperationException("Owner ID does not belong to vendor");
     	}
    }

    public GnuCashEmployee    getEmployee() {
    	if ( jiType == JIType.INVOICE &&
     		 invcType == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
     		return myFile.getEmployeeByID(ownerID);
     	} else {
     		throw new UnsupportedOperationException("Owner ID does not belong to employee");
     	}
    }

    // ----------------------------

    public GnuCashGenerJob    getGenerJob() {
    	if ( jiType == JIType.INVOICE &&
      		 invcType == GnuCashGenerInvoice.TYPE_JOB ) {
      		return myFile.getGenerJobByID(ownerID);
      	} else {
      		throw new UnsupportedOperationException("Owner ID does not belong to job");
      	}
    }
    
    public GnuCashCustomerJob getCustomerJob() {
    	GnuCashGenerJob generJob = getGenerJob();
    	if ( generJob.getOwnerType() == GnuCashGenerJob.TYPE_CUSTOMER ) {
    		return myFile.getCustomerJobByID(ownerID);
    	} else {
      		throw new UnsupportedOperationException("Owner ID does not belong to job or job owner is not a customer");
    	}
    }

    public GnuCashVendorJob   getVendorJob() {
    	GnuCashGenerJob generJob = getGenerJob();
    	if ( generJob.getOwnerType() == GnuCashGenerJob.TYPE_CUSTOMER ) {
    		return myFile.getVendorJobByID(ownerID);
    	} else {
      		throw new UnsupportedOperationException("Owner ID does not belong to job or job owner is not a vendor");
    	}
    }
}
