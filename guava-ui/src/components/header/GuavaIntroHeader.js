import React from 'react'

import {useHistory} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import {WingBlank} from 'antd-mobile';

const cx = classNames.bind(styles);

const GuavaIntroHeader = () => {
    const history = useHistory();

    return (
        <>
            <div className={cx('header_container')}>
                <div className={cx('title_container')}>
                    <WingBlank>
                        <div className={cx('left')} onClick={() => history.push('/')}>
                            <ArrowLeftOutlined/>
                        </div>
                    </WingBlank>
                    <div className={cx('center')}>
                        {/*<span className={cx('title')}>우리동네</span>*/}
                        {/*<span className={cx('sub_title')}>아파트</span>*/}
                    </div>
                    <WingBlank>
                        <div className={cx('right')}>
                            <ArrowLeftOutlined style={{visibility: 'hidden'}}/>
                        </div>
                    </WingBlank>
                </div>
            </div>
        </>
    )
};

export default GuavaIntroHeader;