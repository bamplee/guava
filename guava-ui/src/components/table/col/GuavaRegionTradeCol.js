import React from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';
import {regionState} from '../../datatool/state';

const cx = classNames.bind(styles);

const GuavaRegionTradeCol = () => {
    // const [region, setRegion] = useRecoilState(regionState);
    const region = useRecoilValue(regionState);

    return (
        <div className={cx('header')}>
            <div className={cx('row')}>
                <div>날짜</div>
            </div>
            {
                region.type !== 'DONG' &&
                <div className={cx('row')}>
                    <div>지역</div>
                </div>
            }
            <div className={cx('row', 'apt_name')}>
                <div>이름</div>
            </div>
            <div className={cx('row')}>
                <div>타입</div>
            </div>
            <div className={cx('row')}>
                <div>층</div>
            </div>
            <div className={cx('row')}>
                <div>가격</div>
            </div>
        </div>
    )
};

export default GuavaRegionTradeCol;