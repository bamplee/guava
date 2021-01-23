export const CHART_COLOR_LIST = ['#2E92FC', '#C50A3D', '#FD583C', '#FD8D2F',
                                '#FEC22D', '#EDDC5F', '#AED364', '#5CC687',
                                '#1DBAAD', '#2F7C9A', '#2F7C9A', '#3D3E6A',
                                '#501A48', '#501A48', '#8F1140'];

export const TABLE_OPTION = {
    TRADE: 'trade',
    MARKET: 'market'
};

export const getStartArea = (startArea) => {
    if (startArea === 0) {
        return 0;
    } else if (startArea === 1) {
        return 50;
    } else if (startArea === 2) {
        return 60;
    } else if (startArea === 3) {
        return 85;
    } else if (startArea === 4) {
        return 105;
    }
};

export const getEndArea = (endArea) => {
    if (endArea === 0) {
        return 0;
    } else if (endArea === 1) {
        return 50;
    } else if (endArea === 2) {
        return 60;
    } else if (endArea === 3) {
        return 85;
    } else if (endArea === 4) {
        return 105;
    } else if (endArea === 5) {
        return 500;
    }
};

export const PLACE_TYPE = {
    REGION: 'REGION',
    BUILDING: 'BUILDING'
};

// export const CATEGORY = {
//     HIGH_END: {type: 'HIGH_END', description: '고급 식당 전문'},
//     TREND: {type: 'TREND', description: '요즘 핫한 인스타'},
//     POPULAR: {type: 'POPULAR', description: '팔로워가 많은 인스타'},
//     PUB: {type: 'PUB', description: '술과 함께하는'},
//     COST_BENEFIT: {type: 'COST_BENEFIT', description: '적당한 가격대'},
// };
//
// // fixme 임시로 프론트 처리 서버로 옮겨야함
// export const USER_DESCRIPTION_SET = {
//     'mat_thagoras': '맛타고라스',
//     'naegung_tasty': '내궁맛집',
//     'foodyinkorea': '푸디인코리아',
//     'hdk_chef': '규슐랭가이드',
//     'songchelin_guide': '송슐랭',
//     'oreo_ming': '맛두더지',
//     'mattamyoung': '맛탐영',
//     'misik_holic': '미식홀릭',
//     'theboy9368': '레드피쉬의식도락',
//     'mr.ricesoup3': '늉삼씨',
//     '___wooniverse___': '우니버스',
//     'ssunny2113': '문썬',
//     '_agarifighter_': '아가리파이터',
//     'soju_anju_': '소주안주',
//     'dduziny_chelin': '뚜슐랭가이드',
//     'kikeudeuman': '정영민맛집여행',
//     'guilty.pleasure27': '길티플레져',
//     'bimirya': '비밀이야',
// };