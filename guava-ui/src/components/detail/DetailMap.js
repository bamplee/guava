/*global kakao*/
import React, {useEffect, useState} from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';
import classNames from 'classnames/bind';
import {useHistory} from 'react-router-dom';

import {fetchSummary} from '../datatool/api';
import {useLocalStorage} from '../common/useLocalStorage';

import {
    centerState,
    filterAreaState,
    levelState,
    regionState,
} from '../datatool/state';

import styles from './detailMap.module.scss';
import {getEndArea, getStartArea} from '../constant';

const cx = classNames.bind(styles);

let map;
let infos = [];
const DetailMap = () => {
    const [storageCenter, setStorageCenter] = useState({lat: 37.3614463, lng: 127.1114893});
    const [storageBounds, setStorageBounds] = useState({
        northEastLng: 127.11962734724047,
        northEastLat: 37.372949217422516,
        southWestLng: 127.1033383430648,
        southWestLat: 37.34989828240438
    });
    const [summary, setSummary] = useState([]);
    const [level, setLevel] = useRecoilState(levelState);
    const filterArea = useRecoilValue(filterAreaState);
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
            // history.replace('/');
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

        let lat = region.lat;
        let lng = region.lng;
        // if (storageCenter) {
        //     lat = storageCenter.lat;
        //     lng = storageCenter.lng;
        // }
        let initCenter = new kakao.maps.LatLng(lat, lng);
        let options = { //지도를 생성할 때 필요한 기본 옵션
            center: initCenter, //지도의 중심좌표.
            level: level //지도의 레벨(확대, 축소 정도)
        };
        map = new kakao.maps.Map(container, options);
        initLatLng();
    };

    return (
        <div id="map" className={cx('h-52')}/>
    )
};

export default DetailMap;