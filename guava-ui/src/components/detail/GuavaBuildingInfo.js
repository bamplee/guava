import React from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';

import {buildingState, regionState} from '../datatool/state';

import classNames from 'classnames/bind';
import styles from './guavaRegionInfo.module.scss';
import {WhiteSpace, WingBlank} from 'antd-mobile';

const cx = classNames.bind(styles);

const GuavaBuildingInfo = () => {
    // const [building, setBuilding] = useRecoilState(buildingState);
    // const [region, setRegion] = useRecoilState(regionState);
    const region = useRecoilValue(regionState);
    const building = useRecoilValue(buildingState);

    // console.log(building);

    return (
        <div className={cx('title_container')}>
            <div className={cx('title')}>
                    <span className={cx('address')}>
                        {region.address}
                    </span>
                {
                    (region.type === 'BUILDING' && building) &&
                    <>
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
                    </>
                }
            </div>
        </div>
    );
};

export default GuavaBuildingInfo;