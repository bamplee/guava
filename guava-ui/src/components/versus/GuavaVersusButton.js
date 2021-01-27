import React, {useEffect, useState} from 'react'
import classNames from 'classnames/bind';

import styles from './guavaVersusButton.module.scss';
import {Button, Tag} from 'antd-mobile';
import {useRecoilValue, useRecoilState} from 'recoil';
import {buildingState, regionState, showVersusSearchState, versusRegionListState} from '../datatool/state';
import PlusOutlined from '@ant-design/icons/es/icons/PlusOutlined';

const cx = classNames.bind(styles);

const GuavaVersusButton = () => {
    const [showVersusSearch, setShowVersusSearch] = useRecoilState(showVersusSearchState);

    return (
        <div className={cx('add_container')}>
            <Button className={cx('tag_add')} inline type="primary"
                    onClick={() => setShowVersusSearch(true)}><PlusOutlined
                style={{marginRight: 5}}/>비교할 지역 / 아파트 추가하기</Button>
        </div>
    )
};


export default GuavaVersusButton;