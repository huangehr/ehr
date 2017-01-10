/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.patient.card

include "TCard.thrift"

service TCardService {
	TCard.TCard getCard(1:string id)
}