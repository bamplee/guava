/* SEARCH */
export const fetchSearch = async (query) => {
    const response = await fetch('/api/v1/guava/search/regions?' + new URLSearchParams({
        query: query
    }));
    return await response.json();
};

export const fetchSearchBuilding = async (query) => {
    const response = await fetch('/api/v1/guava/search/buildings?' + new URLSearchParams({
        query: query
    }));
    return await response.json();
};

export const getBuilding = async (buildingId) => {
    const response = await fetch(`/api/v1/guava/search/buildings/${buildingId}`);
    return await response.json();
};

export const getRegion = async (regionId) => {
    const response = await fetch(`/api/v1/guava/search/regions/${regionId}`);
    return await response.json();
};
/* END */
export const fetchSummary = async (level, northEastLng, northEastLat, southWestLng, southWestLat, startArea, endArea) => {
    const response = await fetch('/api/v1/guava/summary?' + new URLSearchParams({
        level: level,
        northEastLng: northEastLng,
        northEastLat: northEastLat,
        southWestLng: southWestLng,
        southWestLat: southWestLat,
        startArea: startArea,
        endArea: endArea
    }));
    return await response.json();
};

// export const fetchCurrentRegion = async (lat, lng) => {
//     const response = await fetch('/api/v1/guava/regions/current?' + new URLSearchParams({
//         lat: lat,
//         lng: lng
//     }));
//     return await response.json();
// };

export const getChart = async (buildingId, areaId, beforeMonth) => {
    const response = await fetch(`/api/v1/guava/chart/buildings/${buildingId}?` + new URLSearchParams({
        areaId: areaId,
        beforeMonth: beforeMonth
    }));
    return await response.json();
};

export const getMatch = async (page) => {
    const response = await fetch(`/api/v1/guava/match?` + new URLSearchParams({
        page: page
    }));
    return await response.json();
};

export const setMatch = async (tradeId, buildingId) => {
    const response = await fetch(`/api/v1/guava/match/check?` + new URLSearchParams({
        tradeId: tradeId,
        buildingId: buildingId,
    }));
    return await response.json();
};

export const getRegionChart = async (regionId, startArea, endArea, beforeMonth) => {
    const response = await fetch(`/api/v1/guava/chart/regions/${regionId}?` + new URLSearchParams({
        startArea: startArea,
        endArea: endArea,
        beforeMonth: beforeMonth
    }));
    return await response.json();
};

export const getDetail = async (buildingId) => {
    const response = await fetch(`/api/v1/guava/buildings/${buildingId}/detail`);
    return await response.json();
};

export const getTrade = async (type, buildingId, page, areaId, date) => {
    const response = await fetch(`/api/v1/guava/${type}/buildings/${buildingId}?` + new URLSearchParams({
        page: page,
        areaId: areaId,
        date: date
    }));
    return await response.json();
};

export const getRegionTrade = async (type, buildingId, page, startArea, endArea, date) => {
    const response = await fetch(`/api/v1/guava/${type}/regions/${buildingId}?` + new URLSearchParams({
        page: page,
        startArea: startArea,
        endArea: endArea,
        date: date
    }));
    return await response.json();
};

// export const getForSale = async (buildingId, page, areaId, date) => {
//     const response = await fetch(`/api/v1/guava/buildings/${buildingId}/market?` + new URLSearchParams({
//         page: page,
//         areaId: areaId,
//         date: date
//     }));
//     return await response.json();
// };
//
// export const getRegionMarketList = async (regionId, page, startArea, endArea, date) => {
//     const response = await fetch(`/api/v1/guava/regions/${regionId}/market?` + new URLSearchParams({
//         page: page,
//         startArea: startArea,
//         endArea: endArea,
//         date: date
//     }));
//     return await response.json();
// };

export const getRegionChildList = async (regionCode, regionType) => {
    const response = await fetch(`/api/v1/guava/search/places/childs?` + new URLSearchParams({
        regionCode: regionCode,
        regionType: regionType
    }));
    return await response.json();
};

export const getBuildings = async (regionId) => {
    const response = await fetch(`/api/v1/guava/search/regions/${regionId}/buildings`);
    return await response.json();
};