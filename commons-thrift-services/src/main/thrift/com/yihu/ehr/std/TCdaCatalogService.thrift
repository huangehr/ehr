/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.std

include "TCdaCatalog.thrift"
include "TCdaDocument.thrift"

service TCdaCatalogService {
	TCdaCatalog.TCdaCatalog getCatalog(1:string id)

	list<TCdaCatalog.TCdaCatalog> getSubCatalog(1:string parentId)

	list<TCdaDocument.TCdaDocument> getDocuments(1:string catalogId)
}
