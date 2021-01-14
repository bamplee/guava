import React from 'react'
import {useRecoilState} from 'recoil';
import classNames from 'classnames/bind';

import {filterAreaState, showAreaFilterState,} from '../datatool/state';

import styles from './guavaMapFilter.module.scss';
import {getEndArea, getStartArea} from '../constant';

const cx = classNames.bind(styles);

const GuavaMapFilter = () => {
    const [showAreaFilter, setShowAreaFilter] = useRecoilState(showAreaFilterState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);

    return (
        <div className={cx('filter_btn_group')}>
            <div className={cx((filterArea[0] === 0 && filterArea[1] === 5) ? 'filter_btn' : 'active_filter_btn')}
                 onClick={() => setShowAreaFilter(!showAreaFilter)}>
                {
                    (filterArea[0] === 0 && filterArea[1] === 5) ?
                        <>
                            면적
                        </> :
                        <div>
                            <div>
                                {getStartArea(filterArea[0])}㎡ ~
                            </div>
                            <div>
                                {
                                    filterArea[1] === 5 ?
                                        '' :
                                        `${getEndArea(filterArea[1])}㎡`
                                }
                            </div>
                        </div>
                }
            </div>
        </div>
    )
};


export default GuavaMapFilter;