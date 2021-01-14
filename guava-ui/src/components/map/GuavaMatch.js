/*global kakao*/
import React, {useEffect, useState} from 'react'
import classNames from 'classnames/bind';

import styles from './guavaMap.module.scss';
import {getMatch, setMatch} from '../datatool/api';
import {Button} from 'antd-mobile';

const cx = classNames.bind(styles);

let map;
let marker = null;
let infos = [];

const GuavaMatch = () => {
        const [loading, setLoading] = useState(false);
        const [summary, setSummary] = useState(null);
        const [page, setPage] = useState(0);

        useEffect(() => {
            initMap();
            // initCenter(lat, lng);
            initMarker();
            // getSummary();

            kakao.maps.event.addListener(map, 'zoom_changed', () => {
            });

            kakao.maps.event.addListener(map, 'dragend', () => {
            });

            kakao.maps.event.addListener(map, 'click', function (mouseEvent) {
            });
        }, []);

        useEffect(() => {
            getSummary();
        }, [page]);

        useEffect(() => {
            drawMap();
        }, [summary]);

        const getSummary = async () => {
            if (loading) {
                return;
            }
            setLoading(true);
            let result = await getMatch(page);
            if (result.originalBuilding) {
                let locPosition = new kakao.maps.LatLng(result.originalBuilding.lat, result.originalBuilding.lng);
                map.setCenter(locPosition);
                map.setLevel(4);
                setSummary(result);
            } else {
                alert('결과없음');
            }
            setLoading(false);
        };

        const setData = async (tradeId, buildingId) => {
            let result = await setMatch(tradeId, buildingId);
            setPage(page + 1);
        };

        const initMarker = () => {
            for (let i = 0; i < infos.length; i++) {
                infos[i].setMap(null);
            }
            infos = [];
        };

        const drawMap = () => {
            if (summary) {
                drawOriginal(summary.originalBuilding);
                summary.compareBuildingList.forEach(x => drawMarker(x));
            }
        };

        const drawOriginal = (x) => {
            let position = new kakao.maps.LatLng(x.lat, x.lng);
            let content = `<div class="match-custom" style="background-color: red;">
                                <div class="price">${x.name}</div>
                           </div>`;

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
            customOverlay.setMap(map);
            infos.push(customOverlay);
        };

        const drawMarker = (x) => {
            let position = new kakao.maps.LatLng(x.lat, x.lng);
            let content = `<div class="match-custom" id="${x.id}">
                                <div class="price">${x.name}</div>
                           </div>`;

// 커스텀 오버레이를 생성합니다
            let customOverlay = new kakao.maps.CustomOverlay({
                clickable: true,
                map: map,
                position: position,
                content: content,
                yAnchor: 1
            });
            customOverlay.id = x.id;
            // customOverlay.type = x.type;
            customOverlay.setMap(map);
            infos.push(customOverlay);

            const elem = document.getElementById(x.id);
            if (elem) {
                elem.addEventListener('click', async () => {
                    setData(summary.originalBuilding.id, x.id);
                });
            }
        };

        const initMap = () => {
            let container = document.getElementById('matchmap'); //지도를 담을 영역의 DOM 레퍼런스
            let options = { //지도를 생성할 때 필요한 기본 옵션
                center: new kakao.maps.LatLng(37.359456, 127.105314), //지도의 중심좌표.
                level: 6 //지도의 레벨(확대, 축소 정도)
            };
            map = new kakao.maps.Map(container, options);
        };

        return (
            <>
                <Button onClick={() => {
                    setData(summary.originalBuilding.id, '-1');
                }} style={{
                    position: 'fixed',
                    bottom: 0,
                    zIndex: 9999,
                    width: '100%',
                    backgroundColor: loading ? 'black' : 'blue',
                    color: 'white'
                }}>{loading ? 'LOADING...' : '패스'}</Button>
                <div id="matchmap" className={cx('map')}/>
            </>
        )
    }
;


export default GuavaMatch;