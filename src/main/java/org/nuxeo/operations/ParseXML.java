package org.nuxeo.operations;

import java.io.Reader;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;
import java.util.Arrays;
import java.util.List;

@Operation(id = ParseXML.ID, category = Constants.CAT_FETCH, label = "ParseXML", description = "Read the XML file and get its content")
public class ParseXML {
	public static final String ID = "Athento.ParseXML";
	
	private static final List<String> FIELDS = Arrays.asList("FileHeader_SchemaVersion", "FileHeader_Modality", "FileHeader_InvoiceIssuerType", "FileHeader_Batch_BatchIdentifier", 
									"FileHeader_Batch_InvoicesCount", "FileHeader_Batch_TotalInvoicesAmount_TotalAmount", "Invoices_Invoice_TaxesOutputs_Tax_TaxableBase_TotalAmount", 
									"Parties_SellerParty_TaxIdentification_PersonTypeCode");
	
	@OperationMethod
	public void run(DocumentModel doc) throws Exception {
		if (doc == null) {
			throw new OperationException(
				"No DocumentModel received. Operation chain must inject a Document in Context");
		}
		
		Blob file_XML = getBlob(doc);

		Reader content = null;

		content = file_XML.getReader();
		
		int enteroLeido;
		char caractLeido = '0';
		String chain1 ="", chain2="", chain3="", chain4="", chain5="", chain6="", chainField="";
		String chainMetadato = "";
		
		boolean chain2Bool=false, chain3Bool=false, chain4Bool=false, chain5Bool=false, chain6Bool=false;
		
		do{
			if(caractLeido != '<'){
			
				enteroLeido = content.read();
				caractLeido = (char) enteroLeido;
			
			} else {
				
				enteroLeido = content.read();
				caractLeido = (char) enteroLeido;

				while(caractLeido != '>'){
					if (chain6Bool){
						if(caractLeido == '/'){
							enteroLeido = content.read();
							caractLeido = (char) enteroLeido;
							if(caractLeido != '>'){
								chain6Bool = false;
								chain5="";
							}
							break;
						} else {
							chain6 +=  Character.toString(caractLeido);
						}
						
						enteroLeido = content.read();
						caractLeido = (char) enteroLeido;
					
					} else if(chain5Bool){
						
						if(caractLeido == '/'){
							enteroLeido = content.read();
							caractLeido = (char) enteroLeido;
							if(caractLeido != '>'){
								chain5Bool = false;
								chain4="";
							}
							break;
						} else {
							chain5 +=  Character.toString(caractLeido);
						}
						
						enteroLeido = content.read();
						caractLeido = (char) enteroLeido;
						
						if(caractLeido == '>'){
							chain6Bool=true;
						}
						
					} else if(chain4Bool){
						if(caractLeido == '/'){
							enteroLeido = content.read();
							caractLeido = (char) enteroLeido;
							if(caractLeido != '>'){
								chain4Bool = false;
								chain3="";
							}
							break;
						} else {
							chain4 +=  Character.toString(caractLeido);
						}
						
						enteroLeido = content.read();
						caractLeido = (char) enteroLeido;
						
						if(caractLeido == '>'){
							chain5Bool=true;
						}
						
					} else if(chain3Bool){
						
						if(caractLeido == '/'){
							enteroLeido = content.read();
							caractLeido = (char) enteroLeido;
							if(caractLeido != '>'){
								chain3Bool = false;
								chain2="";
							}
							break;
						} else {
							chain3 +=  Character.toString(caractLeido);
						}
						
						enteroLeido = content.read();
						caractLeido = (char) enteroLeido;
						
						if(caractLeido == '>'){
							chain4Bool=true;
						}
						
					} else if(chain2Bool){
						
						if(caractLeido == '/'){
							enteroLeido = content.read();
							caractLeido = (char) enteroLeido;
							if(caractLeido != '>'){
								chain2Bool = false;
								chain1="";
							}
							break;
						} else {
							chain2 +=  Character.toString(caractLeido);
						}
						
						enteroLeido = content.read();
						caractLeido = (char) enteroLeido;
						
						if(caractLeido == '>'){
							chain3Bool=true;
						}
						
					} else{
						
						if(caractLeido == '/' || caractLeido == '?'){
							chain1="";
							break;
						} else {
							chain1 +=  Character.toString(caractLeido);
						}
						
						enteroLeido = content.read();
						caractLeido = (char) enteroLeido;
						
						if(caractLeido == '>'){
							chain2Bool=true;
						}
						
					}
					
				}
				
				if(caractLeido == '>'){
					
					enteroLeido = content.read();
					caractLeido = (char) enteroLeido;
					
					if(caractLeido != '<' && caractLeido != '\n'){
						if(chain6 != ""){ chainField = chain1 + "_" + chain2 + "_" + chain3 + "_" + chain4 + "_" + chain5 + "_" + chain6; }
						else if(chain5 != ""){ chainField = chain1 + "_" + chain2 + "_" + chain3 + "_" + chain4 + "_" + chain5; }
						else if(chain4 != ""){ chainField = chain1 + "_" + chain2 + "_" + chain3 + "_" + chain4; }
						else if(chain3 != ""){ chainField = chain1 + "_" + chain2 + "_" + chain3; }
						else if(chain2 != ""){ chainField = chain1 + "_" + chain2; }
						else { chainField = chain1; }
						
						do{ 
							chainMetadato += Character.toString(caractLeido);
							enteroLeido = content.read();
							caractLeido = (char) enteroLeido;
						}while(caractLeido != '<');
						
						for(int cont = 0; cont < FIELDS.size(); cont++){
							if(chainField.equals(FIELDS.get(cont))){
								doc = updateProperty(doc, chainField, chainMetadato);
								break;
							}
						}

						chainField ="";
						chainMetadato = "";
					}
					
				}
				
			}

		}while(enteroLeido!=-1);
		
		content.close();
		
	}
	

    private Blob getBlob(DocumentModel doc) throws Exception {
        Blob blob = (Blob) doc.getPropertyValue("Esq_Factura:Arch_XML");
        if (blob==null) {
            throw new Exception(
            	"No file uploaded");
        }
        
        return blob;
    }
    
    private DocumentModel updateProperty(DocumentModel doc, String xpath, String value) throws Exception {
        Property p = doc.getProperty(xpath);
        
        p.setValue(value);

        return doc;
    }
}
