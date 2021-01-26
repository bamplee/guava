// /*global kakao*/
import React, {useEffect, useState} from 'react'
import {useRecoilValue, useRecoilState} from 'recoil';

import {regionState} from '../datatool/state';

import classNames from 'classnames/bind';
import styles from './guavaRegionInfo.module.scss';
import {getBuilding, getDetail} from '../datatool/api';
import GuavaLoading from './GuavaLoading';
import {Flex, Tabs} from 'antd-mobile';

const cx = classNames.bind(styles);

const GuavaBuildingInfo = () => {
    // const [building, setBuilding] = useRecoilState(buildingState);
    // const [region, setRegion] = useRecoilState(regionState);
    const region = useRecoilValue(regionState);
    const [building, setBuilding] = useState(null);

    // console.log(building);

    useEffect(() => {
        const init = async () => {
            if(region && region.buildingId) {
                setBuilding(await getDetail(region.buildingId));
            }
        };
        init();
//         let staticMapContainer = document.getElementById('image-map'), // 이미지 지도를 표시할 div
//             staticMapOption = {
//                 center: new kakao.maps.LatLng(region.lat, region.lng), // 이미지 지도의 중심좌표
//                 level: 4 // 이미지 지도의 확대 레벨
//             };
//
// // 이미지 지도를 표시할 div와 옵션으로 이미지 지도를 생성합니다
//         let staticMap = new kakao.maps.Map(staticMapContainer, staticMapOption);
//         let marker = new kakao.maps.Marker({
//             map: staticMap,
//             position: new kakao.maps.LatLng(region.lat, region.lng)
//         });
    }, [region]);

    return (
        <div className={cx('title_container')}>
            {
                building ?
                    <div className={cx('title')}>
                        <span className={cx('address')}>
                            {building.address}
                        </span>
                        <span className={cx('info')}>
                            <span>{building.since}</span>
                            <span className={cx('txt_bar')}/>
                            <span>용적률 {`${building.floorAreaRatio}%`}</span>
                            <span className={cx('txt_bar')}/>
                        <span>건폐율 {`${building.buildingCoverageRatio}%`}</span>
                        </span>
                        <span className={cx('info')}>
                            <span>{building.buildingCount}개동</span>
                            <span className={cx('txt_bar')}/>
                            <span>{building.minFloor}-{building.maxFloor}층</span>
                            <span className={cx('txt_bar')}/>
                            <span>{building.hoCount}세대</span>
                            <span className={cx('txt_bar')}/>
                            <span>주차 {building.parkingTotal}대</span>
                        </span>
                    </div> :
                    <div className={cx('title')}>
                        <span className={cx('address')}>
                            {region?.address}
                        </span>
                    </div>
            }
        </div>
    );
};

export default GuavaBuildingInfo;