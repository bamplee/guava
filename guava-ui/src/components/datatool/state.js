import {atom} from 'recoil';

export const centerState = atom({
    key: 'centerState',
    default: {lat: 37.3614463, lng: 127.1114893}
});

export const areaTypeState = atom({
    key: 'areaTypeState',
    default: {areaId: ''}
});

export const tradeDateState = atom({
    key: 'tradeDateState',
    default: null
});

export const buildingState = atom({
    key: 'buildingState',
    default: null
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
    default: null
});

export const versusRegionListState = atom({
    key: 'versusRegionListState',
    default: []
});

export const showAreaFilterState = atom({
    key: 'showAreaFilterState',
    default: false
});

export const showAreaTypeFilterState = atom({
    key: 'showAreaTypeFilterState',
    default: false
});

export const showVersusSearchState = atom({
    key: 'showVersusSearchState',
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