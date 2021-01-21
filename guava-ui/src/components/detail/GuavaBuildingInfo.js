/*global kakao*/
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
        let staticMapContainer = document.getElementById('image-map'), // 이미지 지도를 표시할 div
            staticMapOption = {
                center: new kakao.maps.LatLng(region.lat, region.lng), // 이미지 지도의 중심좌표
                level: 4 // 이미지 지도의 확대 레벨
            };

// 이미지 지도를 표시할 div와 옵션으로 이미지 지도를 생성합니다
        let staticMap = new kakao.maps.Map(staticMapContainer, staticMapOption);
        let marker = new kakao.maps.Marker({
            map: staticMap,
            position: new kakao.maps.LatLng(region.lat, region.lng)
        });
    }, [region]);

    return (
        <>
            <div id='image-map' style={{height: 200}}/>
            <div className={cx('info_container')}>
                <div className={cx('title')}>
                    {region?.name}
                </div>
                <div className={cx('address')}>
                    {region?.address}
                </div>
                {
                    building &&
                    <div className={cx('table')}>
                        <Flex>
                            <Flex.Item>
                                <div className={cx('item')}>
                                    <div className={cx('key')}>입주</div>
                                    <div className={cx('value')}>{building.since}</div>
                                </div>
                            </Flex.Item>
                            <Flex.Item>
                                <div className={cx('item')}>
                                    <div className={cx('key')}>타입</div>
                                    <div className={cx('value')}>{building.aptType}</div>
                                </div>
                            </Flex.Item>
                        </Flex>
                        <Flex>
                            <Flex.Item>
                                <div className={cx('item')}>
                                    <div className={cx('key')}>용적률</div>
                                    <div className={cx('value')}>{building.floorAreaRatio > 0 ? building.floorAreaRatio + '%' : '-'}</div>
                                </div>
                            </Flex.Item>
                            <Flex.Item>
                                <div className={cx('item')}>
                                    <div className={cx('key')}>건폐율</div>
                                    <div className={cx('value')}>{building.buildingCoverageRatio > 0 ? building.buildingCoverageRatio + '%' : '-'}</div>
                                </div>
                            </Flex.Item>
                        </Flex>
                        <Flex>
                            <Flex.Item>
                                <div className={cx('item')}>
                                    <div className={cx('key')}>세대수</div>
                                    <div className={cx('value')}>{building.hoCount}세대</div>
                                </div>
                            </Flex.Item>
                            <Flex.Item>
                                <div className={cx('item')}>
                                    <div className={cx('key')}>주차</div>
                                    {
                                        building.parkingTotal.length > 0 ?
                                            <div className={cx('value')}>{building.parkingTotal}대
                                                (세대당 {(building.parkingTotal / building.hoCount).toFixed(2)}대)
                                            </div> :
                                            '-'
                                    }
                                </div>
                            </Flex.Item>
                        </Flex>
                        <Flex>
                            <Flex.Item>
                                <div className={cx('item')}>
                                    <div className={cx('key')}>저층/고층</div>
                                    <div className={cx('value')}>{building.minFloor}층/{building.maxFloor}층</div>
                                </div>
                            </Flex.Item>
                            <Flex.Item>
                                <div className={cx('item')}>
                                    <div className={cx('key')}>동수</div>
                                    <div className={cx('value')}>{building.buildingCount}개동</div>
                                </div>
                            </Flex.Item>
                        </Flex>
                    </div>
                }
                {/*<div className={cx('area_list')}>*/}
                {/*    {*/}
                {/*        building && building.areaList.length > 0 &&*/}
                {/*        <Tabs onChange={(e) => {*/}
                {/*        }} tabs={building.areaList.map(x => {*/}
                {/*            return {title: x.name}*/}
                {/*        })}*/}
                {/*              initialPage={0}*/}
                {/*              animated={false}*/}
                {/*              useOnPan={false}/>*/}
                {/*    }*/}
                {/*</div>*/}
            </div>
        </>
    );
};

export default GuavaBuildingInfo;