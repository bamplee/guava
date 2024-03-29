/*global kakao*/
import React, {useEffect, useState} from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';
import classNames from 'classnames/bind';
import {useHistory} from 'react-router-dom';
import {Badge} from 'antd-mobile';

import {fetchSummary} from '../datatool/api';
import {useLocalStorage} from '../common/useLocalStorage';

import {
    centerState,
    currentRegionState,
    filterAreaState,
    levelState,
    regionState,
    showVersusSearchState,
    summaryState, versusRegionListState,
} from '../datatool/state';

import styles from './guavaMap.module.scss';
import {getEndArea, getStartArea} from '../constant';
import LoadingOutlined from '@ant-design/icons/es/icons/LoadingOutlined';
import AimOutlined from '@ant-design/icons/es/icons/AimOutlined';
import GuavaMapFilter from './GuavaMapFilter';
import GuavaMapLabel from './GuavaMapLabel';

const cx = classNames.bind(styles);

let map;
let infos = [];
let marker = null;
const GuavaMap = () => {
    const [center, setCenter] = useRecoilState(centerState);
    const [storageCenter, setStorageCenter] = useLocalStorage('storageCenter', {lat: 37.3614463, lng: 127.1114893});
    const [storageBounds, setStorageBounds] = useLocalStorage('storageBounds', {
        northEastLng: 127.11962734724047,
        northEastLat: 37.372949217422516,
        southWestLng: 127.1033383430648,
        southWestLat: 37.34989828240438
    });
    const [showGeoLoading, setShowGeoLoading] = useState(false);
    const [summary, setSummary] = useState([]);
    const [level, setLevel] = useRecoilState(levelState);
    const filterArea = useRecoilValue(filterAreaState);
    // const [center, setCenter] = useRecoilState(centerState);
    // const [bounds, setBounds] = useState(null);
    // const summary = useRecoilValue(summaryQuery);
    const region = useRecoilValue(regionState);

    const history = useHistory();

    window.addEventListener('resize', () => {
        // We execute the same script as before
        let vh = window.innerHeight * 0.01;
        document.documentElement.style.setProperty('--vh', `${vh}px`);
    });

    useEffect(() => {
        // setCenter(storageCenter);
        let vh = window.innerHeight * 0.01;
        document.documentElement.style.setProperty('--vh', `${vh}px`);

        initMap();
        initMarker();
        // getSummary();

        kakao.maps.event.addListener(map, 'zoom_changed', () => {
            setLevel(map.getLevel());
            initLatLng();
        });

        kakao.maps.event.addListener(map, 'dragend', () => {
            history.replace('/');
            initLatLng();
        });

        kakao.maps.event.addListener(map, 'click', () => {
            history.replace('/');
        });

        kakao.maps.event.addListener(map, 'tilesloaded', function () {
            // console.log('loaded');
        });
    }, []);

    useEffect(() => {
        draw();
    }, [summary]);

    useEffect(() => {
        if (storageBounds) {
            getSummary();
        }
    }, [storageBounds]);

    useEffect(() => {
        initMarker();
        // getSummary();
    }, [level, filterArea]);

    const initLatLng = () => {
        const lat = map.getCenter().getLat();
        const lng = map.getCenter().getLng();
        const northEastLng = map.getBounds().getNorthEast().getLng();
        const northEastLat = map.getBounds().getNorthEast().getLat();
        const southWestLng = map.getBounds().getSouthWest().getLng();
        const southWestLat = map.getBounds().getSouthWest().getLat();
        setStorageCenter({lat: lat, lng: lng});
        setCenter({lat: lat, lng: lng});
        setStorageBounds({
            northEastLng: northEastLng,
            northEastLat: northEastLat,
            southWestLng: southWestLng,
            southWestLat: southWestLat
        });
    };

    const initMarker = () => {
        for (let i = 0; i < infos.length; i++) {
            infos[i].setMap(null);
        }
        infos = [];
    };

    const getRegionType = () => {
        if (level > 9) {
            return 'SIDO';
        }
        if (level > 7) {
            return 'SIGUNGU';
        }
        if (level > 5) {
            return 'DONG';
        }
        return 'BUILDING';
    };

    const getSummary = async () => {
        let result = await fetchSummary(level, storageBounds.northEastLng, storageBounds.northEastLat, storageBounds.southWestLng, storageBounds.southWestLat, getStartArea(filterArea[0]), getEndArea(filterArea[1]));
        // console.log(result);
        setSummary(result);
    };

    const draw = () => {
        summary.filter(x => x.type === getRegionType()).filter(x => !infos.map(y => y.id).includes(x.id)).forEach(x => {
            let position = new kakao.maps.LatLng(x.lat, x.lng);
            let content = `
                <div class="bg-guava ring-guava-dark rounded-md w-11 border border-guava-dark text-center cursor-pointer p-1 pb-1 customoverlay" id="${x.id}">
                    <div style="font-size: 8px" class="text-white font-medium">${x.name}</div>
                    <div class="text-white text-xs font-medium">${x.price}</div>
                    <div class="text-yellow-300 text-xs font-medium">${x.marketPrice == 0 || x.marketPrice == null ? '-' : x.marketPrice}</div>
                </div>
            `;
            // if (x.type === 'BUILDING') {
            //     content = buildingMarker(x);
            // } else {
            //     content = regionMarker(x);
            // }

// 커스텀 오버레이를 생성합니다
            let customOverlay = new kakao.maps.CustomOverlay({
                clickable: true,
                map: map,
                position: position,
                content: content,
                yAnchor: 1
            });
            customOverlay.id = x.id;
            customOverlay.type = x.type;
            // customOverlay.setMap(map);
            const elem = document.getElementById(x.id);
            if (elem) {
                elem.addEventListener('click', async () => {
                    if (x.type === 'BUILDING') {
                        history.push('/b/' + x.id);
                    } else {
                        history.push('/r/' + x.id);
                    }
                });
            }
            // return customOverlay;
            infos.push(customOverlay);
        });
    };

    const initMap = () => {
        let container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스

        let lat = 37.3614463;
        let lng = 127.1114893;
        if (storageCenter) {
            lat = storageCenter.lat;
            lng = storageCenter.lng;
        }
        let initCenter = new kakao.maps.LatLng(lat, lng);
        let options = { //지도를 생성할 때 필요한 기본 옵션
            center: initCenter, //지도의 중심좌표.
            level: level //지도의 레벨(확대, 축소 정도)
        };
        map = new kakao.maps.Map(container, options);
        initLatLng();
    };

    const currentPosition = () => {
        // HTML5의 geolocation으로 사용할 수 있는지 확인합니다
        if (navigator.geolocation) {
            setShowGeoLoading(true);
            // GeoLocation을 이용해서 접속 위치를 얻어옵니다
            navigator.geolocation.getCurrentPosition(position => {
                let latitude = position.coords.latitude, // 위도
                    longitude = position.coords.longitude; // 경도

                // setLat(latitude);
                // setLng(longitude);
                // setCenter({lat: latitude, lng: longitude});

                let locPosition = new kakao.maps.LatLng(latitude, longitude); // 마커가 표시될 위치를 geolocation으로 얻어온 좌표로 생성합니다
                map.setCenter(locPosition);
                // 마커를 생성합니다
                if (marker) {
                    marker.setImage(null);
                    marker.setMap(null);
                    marker = null;
                }
                marker = new kakao.maps.Marker({
                    map: map,
                    position: locPosition
                });
                marker.setImage(new kakao.maps.MarkerImage('/images/ic-24-currentlocation.svg', new kakao.maps.Size(25, 29)));
                setShowGeoLoading(false);
            });
        } else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다
            alert('error');
            setShowGeoLoading(false);
        }
    };

    const ControlPanel = () => {
        return (
            <div className={cx('map_btn_group')}>
                {
                    showGeoLoading ?
                        <div className={cx('location_loading')}>
                            <LoadingOutlined/>
                        </div>
                        :
                        <div className={cx('location')} onClick={() => currentPosition()}>
                            <AimOutlined/>
                        </div>
                }
            </div>
        )
    };

    const buildingMarker = (x) => {
        return `<div class="customoverlay" id="${x.id}">
                                <div class="name">${x.name}</div>
                                <div class="price" style="display: ${x.price !== '0' ? '' : 'none;'}">${x.price}</div>
                                <div class="market_price" style="display: ${x.marketPrice !== '0' ? '' : 'none;'}">${x.marketPrice}</div>
<!--                                <div class="market_price" style="display: ${x.marketPrice ? '' : 'none;'}">${x.marketPrice}</div>-->
                           </div>`;
    };

    const regionMarker = (x) => {
        return `<div class="customoverlay" id="${x.id}">
                                <div class="name">${x.name}</div>
                                <div class="price" style="display: ${x.price !== '0' ? '' : 'none;'}">${x.price}</div>
<!--                                <div class="market_price" style="display: ${x.marketPrice !== '0' ? '' : 'none;'}">${x.marketPrice}</div>-->
                           </div>`;
    };

    return (
        <>
            <GuavaMapLabel/>
            <ControlPanel/>
            <GuavaMapFilter/>
            <div id="map" className={cx('map')}/>
        </>
    )
};

export default GuavaMap;