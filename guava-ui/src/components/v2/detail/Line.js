import React from 'react'

import classNames from 'classnames/bind';

import styles from './line.module.scss';

const cx = classNames.bind(styles);

const Line = () => {

    return (
        <div className={cx('h-2 bg-gray-200 border-t border-gray-300')}/>
    )
};

export default Line;