import React, {useEffect, useState} from 'react'
import {useRecoilValue, useRecoilState} from 'recoil';

import {regionState} from '../datatool/state';

import classNames from 'classnames/bind';
import styles from './guavaRegionInfo.module.scss';
import {getBuilding, getDetail} from '../datatool/api';
import GuavaLoading from './GuavaLoading';

const cx = classNames.bind(styles);

const GuavaBuildingInfo = () => {
    // const [building, setBuilding] = useRecoilState(buildingState);
    // const [region, setRegion] = useRecoilState(regionState);
    const region = useRecoilValue(regionState);
    const [building, setBuilding] = useState(null);

    // console.log(building);

    useEffect(() => {
        const init = async () => {
            setBuilding(await getDetail(region.buildingId));
        };
        if(region.type === 'BUILDING') {
            init();
        }
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
                            {region.address}
                        </span>
                    </div>
            }
        </div>
    );
};

export default GuavaBuildingInfo;