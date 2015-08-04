package org.nuxeo.operations;

import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.runtime.api.Framework;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.List;

@Operation(id = ParseXML.ID, category = Constants.CAT_FETCH, label = "ParseXML", description = "Read the XML file and get its content")
public class ParseXML {
	public static final String ID = "Athento.ParseXML";
	
	private static String chain;
	
	private static int contador1, contador2;
	
	private static final List<String> FIELDS = Arrays.asList("FileHeader_SchemaVersion", "FileHeader_Modality", "FileHeader_InvoiceIssuerType", "FileHeader_Batch_BatchIdentifier", 
									"FileHeader_Batch_InvoicesCount", "FileHeader_Batch_TotalInvoicesAmount_TotalAmount", "FileHeader_Batch_TotalOutstandingAmount_TotalAmount",
									"FileHeader_Batch_TotalExecutableAmount_TotalAmount", "FileHeader_Batch_InvoiceCurrencyCode", "Parties_SellerParty_TaxIdentification_PersonTypeCode",
									"Parties_SellerParty_TaxIdentification_ResidenceTypeCode", "Parties_SellerParty_TaxIdentification_TaxIdentificationNumber", "Parties_SellerParty_LegalEntity_CorporateName",
									"Parties_SellerParty_LegalEntity_AddressInSpain_Address", "Parties_SellerParty_LegalEntity_AddressInSpain_PostCode", "Parties_SellerParty_LegalEntity_AddressInSpain_Town",
									"Parties_SellerParty_LegalEntity_AddressInSpain_Province", "Parties_SellerParty_LegalEntity_AddressInSpain_CountryCode", "Parties_BuyerParty_TaxIdentification_PersonTypeCode", 
									"Parties_BuyerParty_TaxIdentification_ResidenceTypeCode", "Parties_BuyerParty_TaxIdentification_TaxIdentificationNumber", "Parties_BuyerParty_LegalEntity_CorporateName", 
									"Parties_BuyerParty_LegalEntity_AddressInSpain_Address", "Parties_BuyerParty_LegalEntity_AddressInSpain_PostCode", "Parties_BuyerParty_LegalEntity_AddressInSpain_Town", 
									"Parties_BuyerParty_LegalEntity_AddressInSpain_Province", "Parties_BuyerParty_LegalEntity_AddressInSpain_CountryCode", "Invoices_Invoice_InvoiceHeader_InvoiceNumber", 
									"Invoices_Invoice_InvoiceHeader_InvoiceDocumentType", "Invoices_Invoice_InvoiceHeader_InvoiceClass", "Invoices_Invoice_InvoiceIssueData_IssueDate", 
									"Invoices_Invoice_InvoiceIssueData_InvoiceCurrencyCode", "Invoices_Invoice_InvoiceIssueData_TaxCurrencyCode", "Invoices_Invoice_InvoiceIssueData_LanguageName", 
									"Invoices_Invoice_TaxesOutputs_Tax_TaxTypeCode", "Invoices_Invoice_TaxesOutputs_Tax_TaxRate", "Invoices_Invoice_TaxesOutputs_Tax_TaxableBase_TotalAmount", 
									"Invoices_Invoice_TaxesOutputs_Tax_TaxAmount_TotalAmount", "Invoices_Invoice_InvoiceTotals_TotalGrossAmount", "Invoices_Invoice_InvoiceTotals_TotalGeneralDiscounts", "Invoices_Invoice_InvoiceTotals_TotalGeneralSurcharges", 
									"Invoices_Invoice_InvoiceTotals_GeneralDiscounts_Discount_DiscountReason", "Invoices_Invoice_InvoiceTotals_GeneralDiscounts_Discount_DiscountRate", "Invoices_Invoice_InvoiceTotals_GeneralDiscounts_Discount_DiscountAmount", 
									"Invoices_Invoice_InvoiceTotals_TotalGrossAmountBeforeTaxes", "Invoices_Invoice_InvoiceTotals_TotalTaxOutputs", "Invoices_Invoice_InvoiceTotals_TotalTaxesWithheld", 
									"Invoices_Invoice_InvoiceTotals_InvoiceTotal", "Invoices_Invoice_InvoiceTotals_TotalOutstandingAmount", "Invoices_Invoice_InvoiceTotals_TotalExecutableAmount", "Invoices_Invoice_InvoiceTotals_TotalReimbursableExpenses", 
									"Invoices_Invoice_Items_InvoiceLine_IssuerContractReference", "Invoices_Invoice_Items_InvoiceLine_IssuerTransactionReference", "Invoices_Invoice_Items_InvoiceLine_ItemDescription", 
									"Invoices_Invoice_Items_InvoiceLine_Quantity", "Invoices_Invoice_Items_InvoiceLine_UnitOfMeasure", "Invoices_Invoice_Items_InvoiceLine_UnitPriceWithoutTax", 
									"Invoices_Invoice_Items_InvoiceLine_TotalCost", "Invoices_Invoice_Items_InvoiceLine_GrossAmount", "Invoices_Invoice_Items_InvoiceLine_TaxesOutputs_Tax_TaxTypeCode", 
									"Invoices_Invoice_Items_InvoiceLine_TaxesOutputs_Tax_TaxRate", "Invoices_Invoice_Items_InvoiceLine_TaxesOutputs_Tax_TaxableBase_TotalAmount", "Invoices_Invoice_Items_InvoiceLine_TaxesOutputs_Tax_TaxAmount_TotalAmount", 
									"Invoices_Invoice_Items_InvoiceLine_TransactionDate", "Parties_SellerParty_Individual_Name", "Parties_SellerParty_Individual_FirstSurname", "Parties_SellerParty_Individual_SecondSurname", 
									"Parties_BuyerParty_Individual_Name", "Parties_BuyerParty_Individual_FirstSurname", "Parties_BuyerParty_Individual_SecondSurname", 
									"Invoices_Invoice_PaymentDetails_Installment_InstallmentDueDate", "Invoices_Invoice_PaymentDetails_Installment_InstallmentAmount", "Invoices_Invoice_PaymentDetails_Installment_PaymentMeans", 
									"Invoices_Invoice_PaymentDetails_Installment_AccountToBeCredited_IBAN", "Invoices_Invoice_AdditionalData_InvoiceAdditionalInformation", "Invoices_Invoice_PaymentDetails_Installment_CollectionAdditionalInformation", 
									"Invoices_Invoice_InvoiceHeader_InvoiceSeriesCode");
	
	@OperationMethod
	public void run(DocumentModel doc) throws Exception {
		if (doc == null) {
			throw new OperationException(
				"No DocumentModel received. Operation chain must inject a Document in Context");
		}
		
		contador1 = 0;
		contador2 = 0;
		
		Blob file_XML = getBlob(doc);
		
		String mainChain="";
		
		String digestFile = file_XML.getDigest();
		
		String Folder1 = Character.toString(digestFile.charAt(0))+Character.toString(digestFile.charAt(1));
		
		String Folder2 = Character.toString(digestFile.charAt(2))+Character.toString(digestFile.charAt(3));
		
		String pathFile = Framework.getProperty("nuxeo.data.dir").toString() + "/binaries/data/" + Folder1 + "/" + Folder2 + "/" + digestFile;
		
		//File rutaOrigen = new File(".").getAbsoluteFile();
		
		File file = new File(pathFile);
		
		if(!file.exists()){
			throw new Exception("File not found");
		}
		
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		Document docum1 = dBuilder.parse(file);
		
		NodeList nodeList = docum1.getChildNodes();
		
		Node tempNode = nodeList.item(0);
		
		NodeList nodeList2 = nodeList;
		
		if(tempNode.getNodeName().equals("FileHeader")){
			nodeList2 = nodeList;
		} else {
			nodeList2 = tempNode.getChildNodes();
		}
		
		for(int i=0; i<nodeList2.getLength(); i++){
		
			Node tempNode2 = nodeList2.item(i);
			if(tempNode2.getNodeType()==Node.ELEMENT_NODE){
				mainChain = tempNode2.getNodeName();
				extraeValoresXMLaMetadatosDocumento(tempNode2, mainChain, doc);
			}
		}
		
		/*if (true) {
			throw new OperationException(
					docum1.getDocumentElement().getNodeName());
		}*/
		
	}
	

    private Blob getBlob(DocumentModel doc) throws Exception {
        Blob blob = (Blob) doc.getPropertyValue("Esq_Factura:Arch_XML");
        if (blob==null) {
            throw new Exception(
            	"No file uploaded");
        }
        
        return blob;
    }
    
    private static void extraeValoresXMLaMetadatosDocumento(Node node, String mainChain, DocumentModel doc) throws DOMException, Exception{
		
		if(node.hasChildNodes() && node.getChildNodes().getLength()!=1){
			NodeList nodeList3 = node.getChildNodes();
			for(int i=0; i<nodeList3.getLength(); i++){
				
				Node node2 = nodeList3.item(i);
				if(node2.getNodeType()==Node.ELEMENT_NODE){
					extraeValoresXMLaMetadatosDocumento(node2, mainChain, doc);
				}
			}
		} else {
			if(node.getParentNode().getNodeName().equals(mainChain)){
				chain = mainChain + "_" + node.getNodeName();
			} else if(node.getParentNode().getParentNode().getNodeName().equals(mainChain)){
				chain = mainChain + "_" + node.getParentNode().getNodeName() + "_" + node.getNodeName();
			} else if(node.getParentNode().getParentNode().getParentNode().getNodeName().equals(mainChain)){
				chain = mainChain + "_" + node.getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getNodeName() + "_" + node.getNodeName();
			} else if(node.getParentNode().getParentNode().getParentNode().getParentNode().getNodeName().equals(mainChain)){
				chain = mainChain + "_" + node.getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getNodeName() + "_" + node.getNodeName();
			} else if(node.getParentNode().getParentNode().getParentNode().getParentNode().getParentNode().getNodeName().equals(mainChain)){
				chain = mainChain + "_" + node.getParentNode().getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getNodeName() + "_" + node.getNodeName();
			} else if(node.getParentNode().getParentNode().getParentNode().getParentNode().getParentNode().getParentNode().getNodeName().equals(mainChain)){
				chain = mainChain + "_" + node.getParentNode().getParentNode().getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getNodeName() + "_" + node.getNodeName();
			} else {
				chain = mainChain + "_" + node.getParentNode().getParentNode().getParentNode().getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getParentNode().getNodeName() + "_" + node.getParentNode().getNodeName() + "_" + node.getNodeName();
			}
			
			for(int cont = 0; cont < FIELDS.size(); cont++){
				
				if(chain.indexOf("Individual_AddressInSpain") != -1){
					chain = chain.replace("Individual", "LegalEntity");
				}
				
				if(chain.equals(FIELDS.get(cont))){
					if(chain.startsWith("Invoices_Invoice_TaxesOutputs_Tax_")){
						chain += contador1;
						if(chain.equals("Invoices_Invoice_TaxesOutputs_Tax_TaxAmount_TotalAmount" + contador1)){
							contador1++;
						}
					}
					
					if(chain.startsWith("Invoices_Invoice_Items_InvoiceLine_")){
						if(chain.equals("Invoices_Invoice_Items_InvoiceLine_TransactionDate")){
							contador2--;
							doc = updateProperty(chain+contador2, node.getTextContent(), doc);
							contador2++;
							break;
						}
						chain += contador2;
						if(chain.equals("Invoices_Invoice_Items_InvoiceLine_TaxesOutputs_Tax_TaxAmount_TotalAmount" + contador2)){
							contador2++;
						}
					}
					
					doc = updateProperty(chain, node.getTextContent(), doc);
					break;
				}
			}
		}
    }
    
    private static DocumentModel updateProperty(String xpath, String value, DocumentModel doc) throws Exception {
        Property p = doc.getProperty(xpath);
        
        p.setValue(value);

        return doc;
    }
}
