import React from 'react'
import {ActivityIndicator} from 'antd-mobile/lib/index';

import classNames from 'classnames/bind';
import styles from './GuavaLoading.module.scss';

const cx = classNames.bind(styles);

const GuavaLoading = ({isLoading}) => {
    return (
        isLoading &&
        <div>
            <ActivityIndicator size="large" className={cx('loading')}/>
            <div className={cx('loading_message')}>로딩중..</div>
        </div>
    )
};

export default GuavaLoading;