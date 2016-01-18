/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.std

include "TCdaDocument.thrift"

service TCdaDocumentService {
	TCdaDocument.TCdaDocument getDocument(1:string id)
}