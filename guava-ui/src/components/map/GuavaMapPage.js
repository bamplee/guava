/*global kakao*/
import React from 'react'
import classNames from 'classnames/bind';

import styles from './guavaMap.module.scss';
import GuavaMainHeader from '../header/GuavaSearch';
import GuavaMap from './GuavaMap';

const cx = classNames.bind(styles);

let map;
let infos = [];
let marker = null;
const GuavaMapPage = () => {
    // // const [map, setMap] = useRecoilState(lngState);
    // const [level, setLevel] = useRecoilState(levelState);
    // const [summary, setSummary] = useState([]);
    // // const [showQueryResult, setShowQueryResult] = useRecoilState(showQueryResultState);
    // const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    // const [region, setRegion] = useRecoilState(regionState);
    // const [center, setCenter] = useRecoilState(centerState);
    // const [bounds, setBounds] = useRecoilState(boundsState);
    //
    // const history = useHistory();


//     useEffect(() => {
//         initMap();
//
//         kakao.maps.event.addListener(map, 'zoom_changed', () => {
//             setLevel(map.getLevel());
//             // setShowQueryResult(false);
//         });
//
//         kakao.maps.event.addListener(map, 'dragend', () => {
//             initLatLng(map);
//             // setLat(map.getCenter().getLat());
//             // setLng(map.getCenter().getLng());
//             // setShowQueryResult(false);
//         });
//
//         // kakao.maps.event.addListener(map, 'click', function (mouseEvent) {
//         //     setShowQueryResult(false);
//         // });
//     }, []);
//
//     useEffect(() => {
//         if (map) {
//             // initMarker();
//             infos = infos.map(x => {
//                 if (x.type === getRegionType()) {
//                 } else {
//                     x.setMap(null);
//                     x = null;
//                 }
//                 return x;
//             }).filter(x => x !== null);
//             // getSummary();
//         }
//     }, [level]);
//
//     useEffect(() => {
//         if (map) {
//             getSummary();
//         }
//     }, [center]);
//
//     useEffect(() => {
//         if (map) {
//             let locPosition = new kakao.maps.LatLng(center.lat, center.lng);
//             map.setCenter(locPosition);
//             // setLat(lat);
//             // setLng(lng);
//             // getSummary();
//         }
//     }, [region]);
//
//     useEffect(() => {
//         drawMap();
//     }, [summary]);
//
//     useEffect(() => {
//         // initCenter(lat, lng);
//         if (map) {
//             initMarker();
//             // getSummary();
//         }
//     }, [filterArea]);
//
//     const initLatLng = (map) => {
//         setCenter({lat: map.getCenter().getLat(), lng: map.getCenter().getLng()});
//         const northEastLng = map.getBounds().getNorthEast().getLng();
//         const northEastLat = map.getBounds().getNorthEast().getLat();
//         const southWestLng = map.getBounds().getSouthWest().getLng();
//         const southWestLat = map.getBounds().getSouthWest().getLat();
//         setBounds({
//             northEastLng: northEastLng,
//             northEastLat: northEastLat,
//             southWestLng: southWestLng,
//             southWestLat: southWestLat
//         });
//     };
//
//     const initMarker = () => {
//         for (let i = 0; i < infos.length; i++) {
//             infos[i].setMap(null);
//         }
//         infos = [];
//     };
//
//     const getRegionType = () => {
//         if (level > 9) {
//             return 'SIDO';
//         }
//         if (level > 7) {
//             return 'SIGUNGU';
//         }
//         if (level > 5) {
//             return 'DONG';
//         }
//         return 'BUILDING';
//     };
//
//     const drawMap = () => {
//         if (summary.length > 0) {
//             summary.filter(x => x.type === getRegionType()).filter(x => !infos.map(x => x.id).includes(x.id)).forEach(x => {
//                 let position = new kakao.maps.LatLng(x.lat, x.lng);
//                 let content = '';
//                 if (x.type === 'BUILDING') {
//                     content = `<div class="customoverlay" id="${x.id}">
//                                 <div class="name">${x.name > 0 ? x.name + '평' : '-'}</div>
//                                 <div class="price" style="display: ${x.price !== '0' ? '' : 'none;'}">${x.price}</div>
//                                 <div class="market_price" style="display: ${x.marketPrice ? '' : 'none;'}">${x.marketPrice}</div>
//                            </div>`;
//                 } else {
//                     content = `<div class="customoverlay" id="${x.id}">
//                                 <div class="name">${x.name}</div>
//                                 <div class="price" style="display: ${x.price !== '0' ? '' : 'none;'}">${x.price}</div>
//                                 <div class="market_price" style="display: ${x.marketPrice !== '0' ? '' : 'none;'}">${x.marketPrice}</div>
//                            </div>`;
//                 }
//
// // 커스텀 오버레이를 생성합니다
//                 let customOverlay = new kakao.maps.CustomOverlay({
//                     clickable: true,
//                     map: map,
//                     position: position,
//                     content: content,
//                     yAnchor: 1
//                 });
//                 customOverlay.id = x.id;
//                 customOverlay.type = x.type;
//                 customOverlay.setMap(map);
//                 const elem = document.getElementById(x.id);
//                 if (elem) {
//                     elem.addEventListener('click', async () => {
//                         if (x.type === 'BUILDING') {
//                             // setPlaceType(PLACE_TYPE.BUILDING);
//                             history.push('/b/' + x.id);
//                         } else {
//                             // setPlaceType(PLACE_TYPE.REGION);
//                             history.push('/r/' + x.id);
//                         }
//                     });
//                 }
//                 infos.push(customOverlay);
//             });
//         }
//     };
//
//     const getSummary = async () => {
//         // return;
//         // if (!isLoading) {
//         // setIsLoading(true);
//         const northEastLng = map.getBounds().getNorthEast().getLng();
//         const northEastLat = map.getBounds().getNorthEast().getLat();
//         const southWestLng = map.getBounds().getSouthWest().getLng();
//         const southWestLat = map.getBounds().getSouthWest().getLat();
//         let result2 = await fetchSummary(level, northEastLng, northEastLat, southWestLng, southWestLat, getStartArea(filterArea[0]), getEndArea(filterArea[1]));
//         console.log(bounds);
//         let result = await fetchSummary(level, bounds.northEastLng, bounds.northEastLat, bounds.southWestLng, bounds.southWestLat, getStartArea(filterArea[0]), getEndArea(filterArea[1]));
//
//         console.log(result2);
//         console.log(result);
//
//         // let region = await fetchCurrentRegion(lat, lng);
//         setSummary(result);
//         // setIsLoading(false);
//         // setRegion(region);
//         // }
//     };
//
//     const initMap = () => {
//         // setLat(region.lat);
//         // setLng(region.lng);
//         let container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
//
//         let options = { //지도를 생성할 때 필요한 기본 옵션
//             center: new kakao.maps.LatLng(center.lat, center.lng), //지도의 중심좌표.
//             level: level //지도의 레벨(확대, 축소 정도)
//         };
//         map = new kakao.maps.Map(container, options);
//     };

    return (
        <div>
            <GuavaMainHeader/>
            {/*<GuavaMapLabel/>*/}
            {/*<GuavaMapFilter/>*/}
            {/*<div id="map" className={cx('map')}/>*/}
            <GuavaMap/>
        </div>
    )
};

export default GuavaMapPage;