/*global kakao*/
import React, {useEffect} from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';
import classNames from 'classnames/bind';

import {currentRegionState, levelState, regionState} from '../datatool/state';

import styles from './guavaMapLabel.module.scss';
import {useLocalStorage} from "../common/useLocalStorage";

const cx = classNames.bind(styles);

const GuavaMapLabel = () => {
    const [level, setLevel] = useRecoilState(levelState);
    const [currentRegion, setCurrentRegion] = useRecoilState(currentRegionState);
    // const [region, setRegion] = useRecoilState(regionState);
    const [center, setCenter] = useLocalStorage('storageCenter', {lat: 37.3614463, lng: 127.1114893});
    const region = useRecoilValue(regionState);

    useEffect(() => {
        if (center) {
            searchAddrFromCoords(center.lng, center.lat, setCurrentName);
        }
    }, [center, level]);

    const setCurrentName = (regionList) => {
        if (regionList.length > 0) {
            let region = regionList[0];
            let name = '';
            // if (level > 8) {
            //     name += region.region_1depth_name;
            // } else if (level > 6) {
            //     name += `${region.region_1depth_name} ${region.region_2depth_name}`;
            // } else {
            //     name += `${region.region_1depth_name} ${region.region_2depth_name} ${region.region_3depth_name}`;
            // }
            name += `${region.region_1depth_name} ${region.region_2depth_name} ${region.region_3depth_name}`;
            region.name = name;
            setCurrentRegion(region);
        }
    };

    const searchAddrFromCoords = (lng, lat, callback) => {
        // 좌표로 행정동 주소 정보를 요청합니다
        let geocoder = new kakao.maps.services.Geocoder();

        geocoder.coord2RegionCode(lng, lat, callback);
    };

    return (
        currentRegion &&
        <div className={cx('region_label')}>
            <span>
                {currentRegion.name}
            </span>
        </div>
    )
};


export default GuavaMapLabel;