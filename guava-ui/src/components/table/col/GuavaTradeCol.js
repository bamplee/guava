import React from 'react'

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaTradeCol = () => {
    return (
        <div className={cx('header')}>
            <div className={cx('row')}>
                <div>날짜</div>
            </div>
            <div className={cx('row')}>
                <div>타입</div>
            </div>
            {/*<div className={cx('row')}>*/}
            {/*    <div>면적</div>*/}
            {/*</div>*/}
            <div className={cx('row')}>
                <div>층</div>
            </div>
            <div className={cx('row')}>
                <div>가격</div>
            </div>
        </div>
    )
};

export default GuavaTradeCol;