import React from 'react'
import {ActivityIndicator} from 'antd-mobile/lib/index';

import classNames from 'classnames/bind';
import styles from './loading.module.scss';

const cx = classNames.bind(styles);

const GuavaLoading = ({isLoading}) => {
    return (
        isLoading &&
        <div className={cx('loading_container')}>
            <ActivityIndicator size="large" className={cx('loading')}/>
        </div>
    )
};

export default GuavaLoading;