/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.patient.card

enum CardType {
	PhysicalCard = 1,
	VirtualCard = 2
}

struct TCard {
	1:required string id
	2:required CardType cardType
	3:optional string socialAccount
	4:optional string cardNo
}