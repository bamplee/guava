import React from 'react'

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaVersusCol = () => {
    return (
        <div className={cx('header')}>
            <div className={cx('row', 'apt_name')}>
                <div>이름</div>
            </div>
            <div className={cx('row')}>
                <div>입주</div>
            </div>
            <div className={cx('row')}>
                <div>용적율</div>
            </div>
            <div className={cx('row')}>
                <div>세대수</div>
            </div>
            <div className={cx('row')}>
                <div>평형</div>
            </div>
            {/*<div className={cx('row')}>*/}
            {/*    <div>가격</div>*/}
            {/*</div>*/}
        </div>
    )
};

export default GuavaVersusCol;