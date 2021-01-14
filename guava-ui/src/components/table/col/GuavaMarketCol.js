import React from 'react'

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaMarketCol = () => {
    return (
        <div className={cx('header')}>
            <div className={cx('row')}>
                <div>날짜</div>
            </div>
            <div className={cx('row')}>
                <div>동</div>
            </div>
            <div className={cx('row')}>
                <div>타입</div>
            </div>
            <div className={cx('row')}>
                <div>층</div>
            </div>
            <div className={cx('row')}>
                <div>전월세</div>
            </div>
            <div className={cx('row')}>
                <div>실거래</div>
            </div>
            <div className={cx('row')}>
                <div>가격</div>
            </div>
        </div>
    )
};

export default GuavaMarketCol;