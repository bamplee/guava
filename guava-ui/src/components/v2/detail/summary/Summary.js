import React, {useEffect, useState} from 'react'
import {useRecoilValue} from 'recoil';

import {regionState} from '../../../datatool/state';
import {getDetail} from '../../../datatool/api';

import classNames from 'classnames/bind';

import styles from './summary.module.scss';

const cx = classNames.bind(styles);

const Summary = () => {
    const region = useRecoilValue(regionState);
    const [building, setBuilding] = useState(null);

    useEffect(() => {
        if (region) {
            initSummary();
        }
    }, [region]);

    const initSummary = async () => {
        if(region.type === 'BUILDING') {
            setBuilding(await getDetail(region.buildingId));
        }
    }

    return (
        <div className={cx('p-5 pb-3 pt-3')}>
            <div className={cx('text-gray-600')}>{region?.address}</div>
            {
                building &&
                <>
                    <div className={cx('flex text-xs')}>
                        <div className={cx('text-gray-400')}>{building.since}</div>
                        <div className={cx('text-gray-100 ml-1 mr-1')}>|</div>
                        <div className={cx('text-gray-400')}>용적률 {building.floorAreaRatio}%</div>
                        <div className={cx('text-gray-100 ml-1 mr-1')}>|</div>
                        <div className={cx('text-gray-400')}>건폐율 {building.buildingCoverageRatio}%</div>
                    </div>
                    <div className={cx('flex text-xs')}>
                        <div className={cx('text-gray-400')}>{building.buildingCount}개동</div>
                        <div className={cx('text-gray-100 ml-1 mr-1')}>|</div>
                        <div className={cx('text-gray-400')}>{building.minFloor}~{building.maxFloor}층</div>
                        <div className={cx('text-gray-100 ml-1 mr-1')}>|</div>
                        <div className={cx('text-gray-400')}>{building.hoCount}세대</div>
                        <div className={cx('text-gray-100 ml-1 mr-1')}>|</div>
                        <div className={cx('text-gray-400')}>주차 {building.parkingTotal}대</div>
                    </div>
                </>
            }
        </div>
    );
};

export default Summary;