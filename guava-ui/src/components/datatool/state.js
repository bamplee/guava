import {atom} from 'recoil';

export const centerState = atom({
    key: 'centerState',
    default: {lat: 37.3614463, lng: 127.1114893}
});

export const boundsState = atom({
    key: 'boundsState',
    default: {
        northEastLng: 127.11994426605064,
        northEastLat: 37.37345347043685,
        southWestLng: 127.10297653864265,
        southWestLat: 37.34943006297289
    }
});

export const summaryState = atom({
    key: 'summaryState',
    default: []
});

export const areaTypeState = atom({
    key: 'areaTypeState',
    default: {areaId: ''}
});

export const tradeDateState = atom({
    key: 'tradeDateState',
    default: null
});

export const showAreaTypeFilterState = atom({
    key: 'showAreaTypeFilterState',
    default: false
});

export const buildingState = atom({
    key: 'buildingState',
    default: {
        'id': 350453,
        'regionId': '22140',
        'name': '느티마을공무원3단지',
        'lat': 37.36759806331969,
        'lng': 127.11128301376961,
        'address': '경기도 성남시 분당구 정자동 88',
        'floorAreaRatio': 178,
        'hoCount': 770,
        'dongCount': 12,
        'aptType': '',
        'buildingCoverageRatio': 15,
        'buildingCount': 12,
        'maxFloor': 25,
        'minFloor': 10,
        'since': '1994년 12월',
        'parkingInside': 157,
        'parkingOutside': 314,
        'parkingTotal': 471,
        'areaList': [{
            'areaId': '350454',
            'type': '84㎡',
            'publicArea': '84.55',
            'privateArea': '58.71',
            'hoCount': '358',
            'name': '25평'
        }, {
            'areaId': '350455',
            'type': '90A㎡',
            'publicArea': '90.93',
            'privateArea': '66.6',
            'hoCount': '202',
            'name': '27평'
        }, {
            'areaId': '350456',
            'type': '92B㎡',
            'publicArea': '92.07',
            'privateArea': '67.43',
            'hoCount': '210',
            'name': '27평'
        }]
    }
});

export const levelState = atom({
    key: 'levelState',
    default: 5
});

export const currentRegionState = atom({
    key: 'currentRegionState',
    default: null
});

export const regionState = atom({
    key: 'regionState',
    default: {
        'type': 'DONG',
        'code': '4113510300',
        'id': '22140',
        'name': '정자동',
        'summaryName': '정자동',
        'address': '성남시 분당구 정자동',
        'lat': 37.3614463,
        'lng': 127.1114893,
    }
});

export const showAreaFilterState = atom({
    key: 'showAreaFilterState',
    default: false
});

export const filterAreaState = atom({
    key: 'filterAreaState',
    default: [0, 5]
});

export const tradeTypeState = atom({
    key: 'tradeTypeState',
    default: 'trade'
});